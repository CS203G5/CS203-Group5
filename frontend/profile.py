import streamlit as st
import requests

API_URL = "http://localhost:8080/profile"


def get_profile(profile_id):
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.get(f"{API_URL}/{st.session_state['profile_id']}", headers=headers)
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
        "privacySettings": privacy_settings,
        "role": role
    }
    headers = {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    response = requests.put(f"{API_URL}/{st.session_state['profile_id']}", json=data, headers=headers)
    if response.status_code == 200:
        st.success("Profile updated successfully.")
        return True
    else:
        st.error(f"Failed to update profile. Status code: {response.status_code}, Response: {response.text}")
        return False

def profile_page():
    st.write(st.session_state)
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

    with st.form("profile_form"):
        username_input = st.text_input("Username", value=profile.get('username', ''))
        email_input = st.text_input("Email", value=profile.get('email', ''))
        bio_input = st.text_area("Bio", value=profile.get('bio', ''))
        privacy_settings_input = st.selectbox(
            "Privacy Settings", 
            ["Public", "Private", "Friends Only"], 
            index=["Public", "Private", "Friends Only"].index(profile.get('privacySettings', 'Public'))
        )
        role_input = st.selectbox(
            "Role",
            ["PLAYER", "ADMIN"],  # Add more roles if needed
            index=["PLAYER", "ADMIN"].index(profile.get('role', 'PLAYER'))
        )

        if st.form_submit_button("Save"):
            update_profile(username_input, email_input, bio_input, privacy_settings_input, role_input)

