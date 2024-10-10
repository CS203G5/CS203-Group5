import streamlit as st
from login_register import login_user, register_user
from profile import profile_page
from matchmaking import matchmaking_page
from RAG import show_rag_assistant

def main():
    if 'jwt_token' not in st.session_state:
        st.sidebar.title("Authentication")
        auth_choice = st.sidebar.radio("Choose an option", ["Login", "Register"])
        if auth_choice == "Login":
            login_user()
        elif auth_choice == "Register":
            register_user()
    else:
        st.sidebar.success(f"Logged in as {st.session_state['username']}")
        page_choice = st.sidebar.radio("Choose an option", ["Profile", "Matchmaking", "AI Assistant", "Logout"])

        if page_choice == "Profile":
            profile_page()
        elif page_choice == "Matchmaking":
            matchmaking_page()
        elif page_choice == "AI Assistant":
            show_rag_assistant()
        elif page_choice == "Logout":
            st.session_state.clear()
            st.success("Logged out!")
            st.experimental_rerun()

if __name__ == "__main__":
    main()
