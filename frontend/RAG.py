import openai
import streamlit as st
import numpy as np
from langchain.text_splitter import CharacterTextSplitter
from langchain.embeddings.openai import OpenAIEmbeddings
from langchain.vectorstores import FAISS
from langchain_community.document_loaders import PyPDFLoader
from langchain.chains import create_history_aware_retriever
from langchain_core.prompts import MessagesPlaceholder
from langchain.chains import create_retrieval_chain
from langchain.chains.combine_documents import create_stuff_documents_chain
from langchain_core.prompts import ChatPromptTemplate
from langchain_community.chat_message_histories import ChatMessageHistory
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.runnables.history import RunnableWithMessageHistory
from faiss import IndexFlatL2
from langchain.docstore import InMemoryDocstore
from dotenv import load_dotenv
import os

load_dotenv()  # Load environment variables from .env
openai.api_key = os.getenv('OPENAI_API_KEY')  # Set OpenAI API key

store = {}

# Define OpenAI model to use
model = "gpt-4o-mini" 

# Define function for generating OpenAI responses
def generate_openai_response(system_prompt, user_prompt, max_tokens=1024, temperature=0.2, top_p=0.7):
    response = openai.ChatCompletion.create(
        model=model,
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt}
        ],
        temperature=temperature,
        top_p=top_p,
        max_tokens=max_tokens,
    )
    return response['choices'][0]['message']['content']

def get_session_history(persona_name: str) -> BaseChatMessageHistory:
    if persona_name not in store:
        store[persona_name] = ChatMessageHistory()
    return store[persona_name]

# Define personas and their corresponding prompts
PERSONAS = {
    f"Base Model: {model}": "",
    "Friendly Fast2Market Specialist": "Hi, I'm here to help with any technical issues. What seems to be the problem?"
}

#######################################

def get_persona_prompt(persona_name):
    """Returns the prompt for the selected persona."""
    return PERSONAS[persona_name]

def get_summary(user_input):
    """Summarizes a QnA transcript using OpenAI's API."""
    system_prompt = "Summarize the following conversation."
    response = generate_openai_response(system_prompt, user_input)
    return response

def format_sources(list_of_referenced_documents):
    """Extracts all text from a list of documents."""
    all_ref_source_n_texts = {i: {} for i in range(len(list_of_referenced_documents))}
    all_texts = ""
    for i, doc in enumerate(list_of_referenced_documents):
        all_ref_source_n_texts[i]['Source'] = doc.metadata['source']
        all_ref_source_n_texts[i]['Text'] = doc.page_content
        all_texts += doc.page_content
    all_ref_source_n_texts['All Texts'] = all_texts
    return all_ref_source_n_texts

def get_response(user_input, conversation_history, persona_name):
    """Generates a response based on the selected persona and conversation history."""
    # Get the persona's system prompt
    system_prompt = get_persona_prompt(persona_name)

    # Build the conversation context
    messages = [
        {"role": "system", "content": system_prompt}
    ]

    for speaker, message in conversation_history:
        role = "user" if speaker == "user" else "assistant"
        messages.append({"role": role, "content": message})
    messages.append({"role": "user", "content": user_input})

    # Retrieve documents related to the persona
    if persona_name not in st.session_state.knowledge_base:
        # Load PDFs
        documents = []
        pdf_folder_path = f'./public/RAGData/'
        if os.path.isdir(pdf_folder_path):
            for file in os.listdir(pdf_folder_path):
                if file.endswith('.pdf'):
                    pdf_path = os.path.join(pdf_folder_path, file)
                    loader = PyPDFLoader(pdf_path)
                    documents.extend(loader.load())

        # Create embeddings
        embeddings = OpenAIEmbeddings()
        if documents:
            text_splitter = CharacterTextSplitter(chunk_size=1000, chunk_overlap=10)
            chunked_documents = text_splitter.split_documents(documents)
            vectorstore = FAISS.from_documents(chunked_documents, embeddings)
        else:
            vectorstore = FAISS(embedding_function=embeddings, index=IndexFlatL2(1536), docstore=InMemoryDocstore(), index_to_docstore_id={})

        st.session_state.knowledge_base[persona_name] = vectorstore
    else:
        vectorstore = st.session_state.knowledge_base[persona_name]

    # Set up retriever
    retriever = vectorstore.as_retriever()

    # Use OpenAI to generate a response
    response = generate_openai_response(system_prompt, user_input)
    
    return response, []  # Placeholder for source_documents (not used in this case)

# Streamlit UI for Fast2Market assistant
st.header("Fast2Market: Your AI assistant here to guide you!", divider="rainbow")

# Initialize session state for storing messages and knowledge base
if "messages" not in st.session_state:
    st.session_state.messages = []

if "knowledge_base" not in st.session_state:
    st.session_state.knowledge_base = {}

# Handle user input and generate responses
if prompt := st.chat_input("Ask any question, like 'How do I submit an instrument request?'"):
    st.session_state.messages.append({"role": "user", "content": prompt})
    responses = []
    response, sourced_texts = get_response(prompt, [(msg["role"], msg["content"]) for msg in st.session_state.messages if msg["role"] == "user" or msg["role"] == "assistant"], "Friendly Fast2Market Specialist")
    responses.append({"role": "assistant", "content": response, "persona": "Friendly Fast2Market Specialist", "sourced_texts": sourced_texts})
    st.session_state.messages.extend(responses)

# Display the conversation history
for message in st.session_state.messages:
    if message["role"] == "user":
        with st.chat_message("user"):
            st.markdown(message["content"])
    else:
        with st.chat_message("assistant"):
            st.markdown(message["content"])

# Summary button for summarizing the latest responses
if "latest_responses" in st.session_state and st.button("Summarize Latest Responses", use_container_width=True):
    st.success("Summary:  \n\n" + get_summary(" ".join([msg["content"] for msg in st.session_state.messages])))
