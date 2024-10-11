import streamlit as st
import requests

API_URL = "http://localhost:8080/profile"

# Initialize the profile ID if it's not already in session state
if 'profile_id' not in st.session_state:
    st.session_state['profile_id'] = 1  # Default profile ID

if 'jwt_token' not in st.session_state:
    st.session_state['jwt_token'] = ""  # or provide a default token if available

if 'username' not in st.session_state:
    st.session_state['username'] = "Guest"  # Default username

def get_profile():
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.get(f"{API_URL}/{st.session_state['profile_id']}", headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        st.error("Failed to load profile.")
        return None

def update_profile(username, email, bio, privacy_settings):
    data = {
        "username": username,
        "email": email,
        "bio": bio,
        "privacySettings": privacy_settings
    }
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.put(f"{API_URL}/{st.session_state['profile_id']}", json=data, headers=headers)
    if response.status_code == 200:
        st.success("Profile updated successfully.")
    else:
        st.error(f"Failed to update profile. Status code: {response.status_code}, Response: {response.text}")

def profile_page():
    st.title("Player Profile")
    st.write(st.session_state['jwt_token'])

    # Fetch the username from session state
    username = st.session_state.get('username', 'Guest')

    # Use default values for other fields
    default_email = "email@example.com"
    default_bio = "This is a default bio."
    default_privacy_settings = "Public"


    # Create the profile form using the username and default values for other fields
    with st.form("profile_form"):
        # Pre-fill the username from the login session
        username_input = st.text_input("Username", value=username, disabled=True)  # Username can't be changed
        
        # Default values for email, bio, and privacy settings
        email_input = st.text_input("Email", value=default_email)
        bio_input = st.text_area("Bio", value=default_bio)
        privacy_settings_input = st.selectbox(
            "Privacy Settings", ["Public", "Private", "Friends Only"], 
            index=["Public", "Private", "Friends Only"].index(default_privacy_settings)
        )

        # Submit button
        submitted = st.form_submit_button("Update Profile")

        # Handle form submission
        if submitted:
            update_profile(username, email_input, bio_input, privacy_settings_input)

