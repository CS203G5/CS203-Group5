import streamlit as st
from login_register import login_user, register_user
from RAG import show_rag_assistant 

def main():
    st.sidebar.title("Navigation")
    choice = st.sidebar.radio("Choose an option", ["Login", "Register", "AI Assistant"]) 

    if choice == "Login":
        login_user()
    elif choice == "Register":
        register_user()
    elif choice == "AI Assistant":
        show_rag_assistant() 

if __name__ == "__main__":
    main()
