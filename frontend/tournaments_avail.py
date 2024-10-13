import streamlit as st
import requests
from datetime import datetime

headers = {
    "Authorization": f"Bearer {st.session_state['jwt_token']}"
}

def get_all_tournaments():

    # Fetch all tournaments data
    try:
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
        participants = requests.get(f'http://localhost:8080/participants/tournament/{tournament_id}', headers=headers)
        if participants.status_code == 200:
            for participant in participants.json():
                if participant['profile']['profileId'] == user_id:
                    return True
        return False
    except requests.exceptions.RequestException as e:
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
                if not is_participant_in_this_tournament(tournament['tournament_id'], user_id):
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
                st.warning("Sign-up closed for this tournament")