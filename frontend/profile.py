import streamlit as st
import requests

API_URL = "http://localhost:8080/profile"

def get_profile(profile_id, jwt_token):
    """Fetch the profile data from the API for the given profile_id."""
    headers = {"Authorization": f"Bearer {jwt_token}"}
    st.write(f"jwt_token: {jwt_token}")
    try:
        response = requests.get(f"{API_URL}/{profile_id}", headers=headers)
        response.raise_for_status()
        profile_data = response.json()
        profile_data['rank'] = get_rank(profile_data.get('rating', 0))
        return profile_data
    except requests.exceptions.RequestException as e:
        st.error(f"Failed to load profile: {e}")
        return None

def get_rank(rating):
    """Determine rank based on rating value."""
    if rating < 200:
        return "Beginner"
    elif rating < 400:
        return "Intermediate"
    elif rating < 800:
        return "Advanced"
    elif rating < 1200:
        return "Expert"
    elif rating < 1400:
        return "Master"
    else:
        return "Grandmaster"

def update_profile(profile_id, jwt_token, data):
    """Send a PUT request to update the profile data."""
    headers = {"Authorization": f"Bearer {jwt_token}"}
    try:
        response = requests.put(f"{API_URL}/{profile_id}", json=data, headers=headers)
        response.raise_for_status()
        st.success("Profile updated successfully.")
        return True
    except requests.exceptions.RequestException as e:
        st.error(f"Failed to update profile: {e}")
        return False

def profile_page():
    """Display the profile page and handle profile updates."""
    # Ensure required session state variables are present
    if 'profile_id' not in st.session_state or 'jwt_token' not in st.session_state:
        st.error("Profile ID or JWT token not found. Please log in.")
        return

    profile_id = st.session_state['profile_id']
    jwt_token = st.session_state['jwt_token']

    # Fetch profile data
    profile = get_profile(profile_id, jwt_token)
    if not profile:
        return  # Error message already displayed in get_profile

    st.title("Player Profile")
    st.subheader(f"Current Rating: {profile.get('rating', 0.0)}")
    st.subheader(f"Rank: {profile.get('rank', 'Unknown')}")
    # st.text(f"Role: {profile.get('role', 'PLAYER')}")

    privacy_options = ["Public", "Private", "Friends Only"]
    privacy_settings = profile.get('privacy_settings', 'Public')
    if privacy_settings not in privacy_options:
        privacy_settings = "Public"

    # Profile update form
    with st.form("profile_form"):
        username_input = st.text_input("Username", value=profile.get('username', ''))
        email_input = st.text_input("Email", value=profile.get('email', ''))
        bio_input = st.text_area("Bio", value=profile.get('bio', ''))
        privacy_settings_input = st.selectbox(
            "Privacy Settings", 
            privacy_options, 
            index=privacy_options.index(privacy_settings)
        )

        if st.form_submit_button("Save"):
            data = {
                "username": username_input,
                "email": email_input,
                "bio": bio_input,
                "privacy_settings": privacy_settings_input,
                "role": profile.get('role', 'PLAYER')
            }
            if update_profile(profile_id, jwt_token, data):
                # Optionally refresh the profile data after successful update
                profile = get_profile(profile_id, jwt_token)

# To run the profile page
if __name__ == "__main__":
    profile_page()
