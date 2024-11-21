import openai
import streamlit as st
from langchain.text_splitter import CharacterTextSplitter
from langchain_community.embeddings import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain_community.document_loaders import PyPDFLoader
from faiss import IndexFlatL2
from langchain_community.docstore.in_memory import InMemoryDocstore
from dotenv import load_dotenv
import os

load_dotenv()  
openai.api_key = os.getenv('OPENAI_API_KEY') 
    
model = "gpt-4o-mini" 
store = {}

def get_response(user_input, conversation_history):
    """Generates a response based on a single system prompt, conversation history, and retrieved vectorstore context."""
    system_prompt = (
        "Hi, I'm here to help with any technical issues. "
        "When answering questions about this platform, refer to it as 'this app' instead of 'the app'. "
        "What seems to be the problem?"
    )

    # Load or retrieve knowledge base
    if "knowledge_base" not in st.session_state or not isinstance(st.session_state.knowledge_base, FAISS):
        # Load the RAG.pdf file
        pdf_path = "./RAG.pdf"
        # pdf_path="/home/ubuntu/CS203-Group5/frontend/RAG.pdf"
        documents = []

        # Check if the file exists
        if os.path.isfile(pdf_path): 
            loader = PyPDFLoader(pdf_path)
            documents.extend(loader.load())
        else:
            st.error(f"PDF file '{pdf_path}' not found.")

        # Create embeddings
        embeddings = OpenAIEmbeddings()
        if documents:
            text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=10)
            chunked_documents = text_splitter.split_documents(documents)
            vectorstore = FAISS.from_documents(chunked_documents, embeddings)
        else:
            # Initialize an empty FAISS vectorstore if no documents are available
            vectorstore = FAISS(
                embedding_function=embeddings, 
                index=IndexFlatL2(1536), 
                docstore=InMemoryDocstore(), 
                index_to_docstore_id={}
            )

        st.session_state.knowledge_base = vectorstore
    else:
        vectorstore = st.session_state.knowledge_base

    # Use vectorstore to retrieve relevant context
    try:
        relevant_docs = vectorstore.similarity_search(user_input, k=5)  # Retrieve top 5 most relevant chunks
    except Exception as e:
        st.error(f"Error during similarity search: {e}")
        relevant_docs = []

    retrieved_context = "\n\n".join([doc.page_content for doc in relevant_docs]) if relevant_docs else "No relevant context found."

    # Build the conversation context
    messages = [
        {"role": "system", "content": f"{system_prompt}\n\nRelevant Context:\n{retrieved_context}"}
    ]

    for speaker, message in conversation_history:
        role = "user" if speaker == "user" else "assistant"
        messages.append({"role": role, "content": message})
    messages.append({"role": "user", "content": user_input})

    # Generate response
    try:
        response = openai.ChatCompletion.create(
            model=model,
            messages=messages,
            temperature=0,
            top_p=0.3,
            max_tokens=1024,
        )
        assistant_response = response.choices[0].message.content
    except Exception as e:
        st.error(f"Error during OpenAI API call: {e}")
        assistant_response = "Sorry, I couldn't process your request due to an error."

    return assistant_response, relevant_docs

# Streamlit UI for AI assistant
def show_rag_assistant():
    st.header("Your AI assistant here to guide you! Ask me anything.")

    # Initialize session state for messages
    if "messages" not in st.session_state:
        st.session_state.messages = []

    # Input prompt from the user
    if prompt := st.chat_input("Ask any question, like 'How do I register on this web app?'"):
        # Add user input to conversation history
        st.session_state.messages.append({"role": "user", "content": prompt})

        # Generate AI response
        response, sourced_texts = get_response(
            prompt,
            [(msg["role"], msg["content"]) for msg in st.session_state.messages]
        )

        # Add AI response to conversation history
        st.session_state.messages.append({"role": "assistant", "content": response})

    # Display conversation messages
    for message in st.session_state.messages:
        with st.chat_message(message["role"]):
            st.markdown(message["content"])
