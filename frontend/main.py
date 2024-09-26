import streamlit as st
from login_register import login_user, register_user, confirm_registration

def main():
    st.sidebar.title("Navigation")
    choice = st.sidebar.radio("Choose an option", ["Login", "Register", "Confirm Registration"])

    if choice == "Login":
        login_user()
    elif choice == "Register":
        register_user()
    elif choice == "Confirm Registration":
        confirm_registration()

if __name__ == "__main__":
    main()
