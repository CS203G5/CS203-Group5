import streamlit as st
import requests
from datetime import datetime

# Fetch tournaments data from the API
response = requests.get('http://localhost:8080/tournament')
if response.status_code == 200:
    tournaments = response.json()
else:
    st.error("Failed to fetch tournaments data")
    tournaments = []

# Fetch the list of tournaments the current user has signed up for
participant_response = requests.get(f'http://localhost:8080/participants/3')
if participant_response.status_code == 200:
    signed_up_tournaments = participant_response.json()
    print(signed_up_tournaments)
    
else:
    st.error("Failed to fetch participant data")
    signed_up_tournaments = []

# Function to check if the tournament date is still valid for sign-up
def is_sign_up_open(tournament_date):
    return datetime.strptime(tournament_date, '%Y-%m-%d') > datetime.now()

def tournaments_avail_page():
    # Display tournaments in a card list
    st.title("Available Tournaments")

    for tournament in tournaments:
        with st.expander(f"{tournament['name']} - {tournament['date']}"):
            st.write(f"**Description:** {tournament['description']}")
            st.write(f"**Location:** {tournament['location']}")
            st.write(f"**Organizer:** {tournament['organizer']}")
            
            if is_sign_up_open(tournament['date']):
                if st.button(f"Sign Up!", key=tournament['tournament_id']):
                    # Implement sign-up logic here
                    user_id = tournament['organizer']
                    if user_id is None:
                        st.error("User not logged in")
                        return

                    payload = {
                        'tournamentId': tournament['tournament_id'],
                        'userId': user_id,
                        'lose': 0,
                        'win': 0,
                        'score': 0.0
                    }

                    register_response = requests.post('http://localhost:8080/participants/register', json=payload)
                    if register_response.status_code == 201:
                        st.success(f"Signed up for {tournament['name']}!")
                    else:
                        # st.error("Failed to sign up for the tournament")
                        st.error(register_response)

            else:
                st.warning("Sign-up closed for this tournament")