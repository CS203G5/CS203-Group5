import requests
import streamlit as st
import random

# Function to fetch all available TournamentIDs
def fetch_tournament_ids():
    response = requests.get("http://localhost:8080/tournaments")
    return response.json()

# Function to fetch participants by TournamentID
def fetch_participants_by_tournament(tournament_id):
    response = requests.get(f"http://localhost:8080/participants/tournament/{tournament_id}")
    return response.json()

# Function to pair participants randomly
def pair_participants(participants):
    random.shuffle(participants)
    pairs = []
    unmatched = None
    for i in range(0, len(participants), 2):
        if i + 1 < len(participants):
            pairs.append((participants[i], participants[i + 1]))
        else:
            unmatched = participants[i]
    return pairs, unmatched

# Function to post matches to the Matches table
def post_match(tournament_id, player1, player2):
    match = {
        "tournamentId": tournament_id,
        "player1": player1,
        "player2": player2
    }
    response = requests.post("http://localhost:8080/participants/matches", json=match)
    return response.status_code == 201

# Streamlit UI
st.title("Tournament Matchmaking")

# Fetch and display available TournamentIDs
tournament_ids = fetch_tournament_ids()
st.write(tournament_ids)  # Debugging step to inspect the structure

# Ensure tournament_ids is a list of dictionaries
if isinstance(tournament_ids, list) and all(isinstance(t, dict) for t in tournament_ids):
    selected_tournament_id = st.selectbox("Select tournamentId", [t['tournamentId'] for t in tournament_ids])
else:
    st.error("Failed to fetch tournament IDs")

if st.button("Generate Matches"):
    participants = fetch_participants_by_tournament(selected_tournament_id)
    pairs, unmatched = pair_participants(participants)
    
    st.write(f"Tournament ID: {selected_tournament_id}")
    
    for pair in pairs:
        player1 = pair[0]['userId']
        player2 = pair[1]['userId']
        if post_match(selected_tournament_id, player1, player2):
            st.write(f"Match: {player1} vs {player2} - Posted Successfully")
        else:
            st.write(f"Match: {player1} vs {player2} - Error Posting")
    
    if unmatched:
        st.write(f"Unmatched Participant: {unmatched['userId']}")