import streamlit as st
import requests
import boto3
from botocore.exceptions import ClientError
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv()
COGNITO_CLIENT_ID = os.getenv('AWS_COGNITO_CLIENT_ID')

# Set backend API URL (replace with actual backend URL if not local)
API_URL = "http://localhost:8080/api/profile"

# Set up AWS Cognito client
client = boto3.client('cognito-idp', region_name='us-east-1')

# Function to log in user with AWS Cognito
def login_user():
    st.title('Login')
    username = st.text_input('Username')
    password = st.text_input('Password', type='password')

    if st.button('Login'):
        try:
            response = client.initiate_auth(
                ClientId=COGNITO_CLIENT_ID,
                AuthFlow='USER_PASSWORD_AUTH',
                AuthParameters={
                    'USERNAME': username,
                    'PASSWORD': password,
                }
            )
            token = response['AuthenticationResult']['IdToken']
            st.session_state['jwt_token'] = token
            st.session_state['username'] = username
            st.success('Login successful!')
            st.experimental_rerun()  # Reload the app to reflect the logged-in state
        except ClientError as e:
            st.error(f"Login failed: {e.response['Error']['Message']}")

# Function to register user with AWS Cognito
def register_user():
    st.title('Register')
    name = st.text_input('Name')
    username = st.text_input('Username')
    email = st.text_input('Email')
    password = st.text_input('Password', type='password')
    gender = st.text_input('Gender')
    phone_number = st.text_input('Phone Number', value='+65')

    if st.button('Register'):
        try:
            response = client.sign_up(
                ClientId=COGNITO_CLIENT_ID,
                Username=username,
                Password=password,
                UserAttributes=[
                    {'Name': 'email', 'Value': email},
                    {'Name': 'gender', 'Value': gender},
                    {'Name': 'phone_number', 'Value': phone_number},
                ]
            )
            st.success('Registration successful! Please check your email for a confirmation code.')
        except ClientError as e:
            st.error(f"Registration failed: {e.response['Error']['Message']}")

# Function to fetch profile information from backend
def get_profile():
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.get(API_URL, headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        st.error("Failed to load profile information.")
        return None

# Function to update profile information
def update_profile(username, email, bio, privacy_settings):
    data = {
        "username": username,
        "email": email,
        "bio": bio,
        "privacySettings": privacy_settings
    }
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.put(API_URL, json=data, headers=headers)
    if response.status_code == 200:
        st.success("Profile updated successfully.")
    else:
        st.error("Failed to update profile.")

# Profile management page
def profile_page():
    st.title("Player Profile")
    profile = get_profile()

    if profile:
        st.subheader("View and Update Profile")
        with st.form("profile_form"):
            username = st.text_input("Username", value=profile["username"])
            email = st.text_input("Email", value=profile["email"])
            bio = st.text_area("Bio", value=profile["bio"])
            privacy_settings = st.selectbox("Privacy Settings",
                                            ["Public", "Private", "Friends Only"],
                                            index=["Public", "Private", "Friends Only"].index(profile["privacySettings"]))

            submitted = st.form_submit_button("Update Profile")
            if submitted:
                update_profile(username, email, bio, privacy_settings)

    st.subheader("Profile Rating Dashboard")
    st.write("Coming soon...")

# Main function to manage app state
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
        page_choice = st.sidebar.radio("Choose an option", ["Profile", "Logout"])

        if page_choice == "Profile":
            profile_page()
        elif page_choice == "Logout":
            st.session_state.clear()
            st.success("Logged out!")
            st.experimental_rerun()
    

if __name__ == "__main__":
    main()
