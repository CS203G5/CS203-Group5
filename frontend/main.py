import streamlit as st
from login_register import login_user, register_user
from RAG import show_rag_assistant  # Import the RAG Assistant function

def main():
    st.sidebar.title("Navigation")
    choice = st.sidebar.radio("Choose an option", ["Login", "Register", "RAG Assistant"])  # Add RAG Assistant option

    if choice == "Login":
        login_user()
    elif choice == "Register":
        register_user()
    elif choice == "RAG Assistant":
        show_rag_assistant()  # Call the RAG Assistant function from RAG.py

if __name__ == "__main__":
    main()
