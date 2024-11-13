import streamlit as st
import requests

from dotenv import load_dotenv
import os

load_dotenv()
API_URL= os.getenv('API_URL')
PROFILE_URL = f"{API_URL}/profile"

def get_profile(profile_id):
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.get(f"{PROFILE_URL}/{profile_id}", headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        st.error("Failed to load profile.")
        return None

def update_profile(username, email, bio, privacy_settings, role):
    data = {
        "username": username,
        "email": email,
        "bio": bio,
        "privacy_settings": privacy_settings,
        "role": role  # Send the role back to the server even though users can't edit it
    }
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.put(f"{PROFILE_URL}/{st.session_state['profile_id']}", json=data, headers=headers)
    if response.status_code == 200:
        st.success("Profile updated successfully.")
        return True
    else:
        st.error(f"Failed to update profile. Status code: {response.status_code}, Response: {response.text}")
        return False

def profile_page():

    if 'profile_id' not in st.session_state:
        st.error("Profile ID not found. Please log in.")
        return

    profile_id = st.session_state['profile_id']
    profile = get_profile(profile_id)
    if profile is None:
        st.error("Failed to load profile.")
        return

    st.title("Player Profile")
    st.subheader(f"Current Rating: {profile.get('rating', 0.0)}")

    # Handle the case where 'privacy_settings' may not be in the expected list
    privacy_options = ["Public", "Private", "Friends Only"]

    privacy_settings = profile.get('privacy_settings', 'Public')
    if privacy_settings not in privacy_options:
        privacy_settings = "Public"  # Fallback if an unexpected value is present

    # Get the current role from the profile (but don't allow it to be edited)
    role = profile.get('role', 'PLAYER')
    st.text(f"Role: {role}")  # Display the role but don't allow editing

    with st.form("profile_form"):
        username_input = st.text_input("Username", value=profile.get('username', ''), disabled=True)
        email_input = st.text_input("Email", value=profile.get('email', ''), disabled=True)
        bio_input = st.text_area("Bio", value=profile.get('bio', ''))
        privacy_settings_input = st.selectbox(
            "Privacy Settings", 
            privacy_options, 
            index=privacy_options.index(privacy_settings)
        )

        if st.form_submit_button("Save"):
            update_profile(username_input, email_input, bio_input, privacy_settings_input, role)
