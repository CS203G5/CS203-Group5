import streamlit as st
import requests

API_URL = "http://localhost:8080/profile"


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
    if 'profile_id' not in st.session_state:
        st.session_state['profile_id'] = 1  # Default profile ID

    if 'username' not in st.session_state:
        st.session_state['username'] = "Guest"  # Default username

    st.title("Player Profile")
    # Fetch the current profile
    profile = get_profile()
    if profile is None:
        # If profile fetching fails, use default values
        profile = {
            'username': st.session_state.get('username', 'Guest'),
            'email': '',
            'bio': '',
            'privacySettings': 'Public',
            'rating': 0.0
        }
    
    st.subheader("Current Rating :  " + str(profile.get('rating', 0.0)))
    with st.form("profile_form"):
        username_input = st.text_input("Username", value=profile.get('username', ''))
        email_input = st.text_input("Email", value=profile.get('email', ''))
        bio_input = st.text_area("Bio", value=profile.get('bio', ''))
        privacy_settings_input = st.selectbox(
            "Privacy Settings", 
            ["Public", "Private", "Friends Only"], 
            index=["Public", "Private", "Friends Only"].index(profile.get('privacySettings', 'Public')))

        submitted = st.form_submit_button("Save")

        if submitted:
            update_profile(username_input, email_input, bio_input, privacy_settings_input)

