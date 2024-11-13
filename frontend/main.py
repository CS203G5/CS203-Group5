import streamlit as st
from login_register import login_user, register_user
from profile import profile_page
from tournament import tournament_page
from tournaments_avail import tournaments_avail_page
from scoreboard_websocket import update_scoreboard

#### DEBUGGING ####
# to keep the files up to date

from RAG import show_rag_assistant

def initialize_session_state():
    if "username" not in st.session_state:
        st.session_state["username"] = None
    if "show_create_form" not in st.session_state:
        st.session_state["show_create_form"] = False
    if "show_update_form" not in st.session_state:
        st.session_state["show_update_form"] = False
    if "selected_tournament_id" not in st.session_state:
        st.session_state["selected_tournament_id"] = None

def main():
    initialize_session_state()
    
    if 'jwt_token' not in st.session_state or st.session_state['jwt_token'] is None:
        st.sidebar.title("Authentication")
        auth_choice = st.sidebar.radio("Choose an option", ["Login", "Register"])
        if auth_choice == "Login":
            login_user()
        elif auth_choice == "Register":
            register_user()
    else:
        st.sidebar.success(f"Logged in as {st.session_state['username']}")
        # if st.session_state['role'] == "PLAYER":
        #     page_choice = st.sidebar.radio("Choose an option", ["Profile", "Tournament", "Update Scoreboard", "AI Assistant", "Logout"])
        # else:
        #     page_choice = st.sidebar.radio("Choose an option", ["Profile", "Available Tournaments", "AI Assistant", "Logout"])
        page_choice = st.sidebar.radio("Choose an option", ["Profile", "Tournament", "Available Tournaments", "Update Scoreboard", "AI Assistant", "Logout"])

        if page_choice == "Profile":
            profile_page()
        elif page_choice == "Tournament":
            tournament_page()
        elif page_choice == "Available Tournaments":
            tournaments_avail_page()
        elif page_choice == "Update Scoreboard":
            update_scoreboard()
        elif page_choice == "AI Assistant":
            show_rag_assistant()
        elif page_choice == "Logout":
            st.session_state.clear()
            st.success("Logged out!")
            st.rerun()

if __name__ == "__main__":
    main()