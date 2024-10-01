import streamlit as st
from login_register import login_user, register_user

def main():
    st.sidebar.title("Navigation")
    choice = st.sidebar.radio("Choose an option", ["Login", "Register"])

    if choice == "Login":
        login_user()
    elif choice == "Register":
        register_user()

if __name__ == "__main__":
    main()
