import openai
import streamlit as st
from langchain.text_splitter import CharacterTextSplitter
from langchain.embeddings.openai import OpenAIEmbeddings
from langchain.vectorstores import FAISS
from langchain_community.document_loaders import PyPDFLoader
from faiss import IndexFlatL2
from langchain.docstore import InMemoryDocstore
from dotenv import load_dotenv
import os

load_dotenv()  
openai.api_key = os.getenv('OPENAI_API_KEY') 

model = "gpt-4o-mini" 
store = {}


def generate_openai_response(system_prompt, user_prompt):
    response = openai.ChatCompletion.create(
        model=model,
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_prompt}
        ],
        temperature=0.2,
        top_p=0.7,
        max_tokens=1024,
    )
    return response.choices[0].message.content

def get_session_history(persona_name: str):
    if persona_name not in store:
        store[persona_name] = []
    return store[persona_name]

PERSONAS = {
    f"Base Model: {model}": "",
    "Friendly Speed Climbing Specialist": "Hi, I'm here to help with any technical issues. What seems to be the problem?"
}

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

    response = generate_openai_response(system_prompt, user_input)
    
    return response, []  # Placeholder for source_documents (not used in this case)

# Streamlit UI for AI assistant
def show_rag_assistant():
    st.header("Your AI assistant here to guide you! Ask me anything.")

    if "messages" not in st.session_state:
        st.session_state.messages = []

    if "knowledge_base" not in st.session_state:
        st.session_state.knowledge_base = {}

    if prompt := st.chat_input("Ask any question, like 'How do I match with a new player?'"):
        st.session_state.messages.append({"role": "user", "content": prompt})
        responses = []
        response, sourced_texts = get_response(
            prompt, 
            [(msg["role"], msg["content"]) for msg in st.session_state.messages if msg["role"] == "user" or msg["role"] == "assistant"], 
            "Friendly Speed Climbing Specialist"
        )
        responses.append({"role": "assistant", "content": response, "persona": "Friendly Speed Climbing Specialist", "sourced_texts": sourced_texts})
        st.session_state.messages.extend(responses)

    for message in st.session_state.messages:
        if message["role"] == "user":
            with st.chat_message("user"):
                st.markdown(message["content"])
        else:
            with st.chat_message("assistant"):
                st.markdown(message["content"])

    if "latest_responses" in st.session_state and st.button("Summarize Latest Responses", use_container_width=True):
        st.success("Summary:  \n\n" + get_summary(" ".join([msg["content"] for msg in st.session_state.messages])))
