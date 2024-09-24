import streamlit as st
import requests

# Set backend API URL (assuming Spring Boot backend is hosted locally)
API_URL = "http://localhost:8080/api/profile"

# Fetch profile information from backend
def get_profile():
    response = requests.get(API_URL)
    if response.status_code == 200:
        return response.json()
    else:
        st.error("Failed to load profile information.")
        return None

def update_profile(username, email, bio, privacy_settings):
    data = {
        "username": username,
        "email": email,
        "bio": bio,
        "privacySettings": privacy_settings
    }
    response = requests.put(API_URL, json=data)
    if response.status_code == 200:
        st.success("Profile updated successfully.")
    else:
        st.error("Failed to update profile.")


st.title("Player Profile")

profile = get_profile()

if profile:
    st.subheader("View and Update Profile")
    
    # Form to update profile details
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

