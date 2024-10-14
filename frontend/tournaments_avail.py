import streamlit as st
import requests
from datetime import datetime

def get_headers():
    if 'jwt_token' in st.session_state:
        return {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    else:
        st.error("JWT token not found in session state.")
        return {}

def get_all_tournaments():

    # Fetch all tournaments data
    try:
        headers = get_headers()
        response = requests.get('http://localhost:8080/tournament', headers=headers)
        if response.status_code == 200:
            all_tournaments = response.json()
            return all_tournaments
        else:
            st.error("Failed to fetch tournaments data")
            all_tournaments = []
            return all_tournaments
    except requests.exceptions.RequestException as e:
        return e

# Check if the tournament date is still valid for sign-up (date is before today)
def is_sign_up_open(tournament_date):
    return datetime.strptime(tournament_date, '%Y-%m-%d') > datetime.now()

def is_participant_in_this_tournament(tournament_id,user_id):
    try:
        headers = get_headers()
        participants = requests.get(f'http://localhost:8080/participants/tournament/{tournament_id}', headers=headers)
        if participants.status_code == 200:
            for participant in participants.json():
                if participant['profile']['profileId'] == user_id:
                    return True
        return False
    except requests.exceptions.RequestException as e:
        return False

def has_duels(tournament_id):
    headers = get_headers()
    if not headers:
        return False

    try:
        response = requests.get(f'http://localhost:8080/api/duel?tid={tournament_id}', headers=headers)
        if response.status_code == 200:
            duels = response.json()
            return len(duels) > 0
        else:
            st.error(f"Failed to fetch duels data: {response.status_code}")
            return False
    except requests.exceptions.RequestException as e:
        st.error(f"Request failed: {e}")
        return False

def tournaments_avail_page():
    
    st.title("Available Tournaments")


    all_tournaments = get_all_tournaments()

    for tournament in all_tournaments:
        with st.expander(f"{tournament['name']} - {tournament['date']}"):
            st.write(f"**Description:** {tournament['description']}")
            st.write(f"**Location:** {tournament['location']}")
            st.write(f"**Rating Type:** {'Random' if tournament['isRandom'] == 1 else 'Similar Rated'}")
            st.write(f"**Organizer:** {tournament['organizer']}")
            
            user_id = st.session_state['profile_id']
            
            if is_sign_up_open(tournament['date']):
                if has_duels(tournament['tournament_id']):
                    st.warning("Sign-ups are closed as matchmaking has passed")
                elif not is_participant_in_this_tournament(tournament['tournament_id'], user_id):
                    if st.button(f"Sign Up!", key=tournament['tournament_id']):
                        if user_id is None:
                            st.error("User not logged in")
                            return

                        payload = {
                            'profile': {
                                'profileId': user_id
                            },
                            "tournament": {
                                "tournament_id": tournament['tournament_id']
                            },
                            'lose': 0,
                            'win': 0
                        }

                        try:
                            headers = get_headers()
                            register_response = requests.post('http://localhost:8080/participants/register', json=payload, headers=headers)
                            if register_response.status_code == 201:
                                st.success(f"Signed up for {tournament['name']}!")
                            else:
                                st.error(register_response)
                        except requests.exceptions.RequestException as e:
                            st.error(register_response, e)
                else:
                    st.warning("You have already signed up for this tournament")
            else:
                st.warning("Sign-up are closed")