import streamlit as st
from login_register import login_user, register_user
from RAG import get_response, get_summary  # Import the necessary RAG functions

def main():
    st.sidebar.title("Navigation")
    choice = st.sidebar.radio("Choose an option", ["Login", "Register", "RAG Assistant"])  # Add RAG Assistant option

    if choice == "Login":
        login_user()
    elif choice == "Register":
        register_user()
    elif choice == "RAG Assistant":
        show_rag_assistant()  # Call the RAG Assistant function

def show_rag_assistant():
    # RAG Assistant logic from RAG.py
    st.header("Fast2Market: Your AI assistant here to guide you!")

    # Initialize session state for storing messages and knowledge base
    if "messages" not in st.session_state:
        st.session_state.messages = []

    if "knowledge_base" not in st.session_state:
        st.session_state.knowledge_base = {}

    # Handle user input and generate responses
    if prompt := st.chat_input("Ask any question, like 'How do I submit an instrument request?'"):
        st.session_state.messages.append({"role": "user", "content": prompt})
        responses = []
        response, sourced_texts = get_response(
            prompt, 
            [(msg["role"], msg["content"]) for msg in st.session_state.messages if msg["role"] == "user" or msg["role"] == "assistant"], 
            "Friendly Fast2Market Specialist"
        )
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

if __name__ == "__main__":
    main()