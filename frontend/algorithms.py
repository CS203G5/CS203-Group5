import streamlit as st
import requests
import trueskill
import random

def get_headers():
    return {"Authorization": f"Bearer {st.session_state['jwt_token']}"}

def get_duels(tournament_id):
    try:
        headers = get_headers()
        response = requests.get(f"http://localhost:8080/api/duel?tid={tournament_id}", headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching duels: {e}")
        return []

def fetch_participants_by_tournament(tournament_id):
    try:
        headers = get_headers()
        response = requests.get(f"http://localhost:8080/participants/tournament/{tournament_id}", headers=headers)
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Failed to retrieve participants by tournament: {e}")

def randomly_pair_participants(participants):
    if not participants:
        st.error("Participants list is empty or not initialized.")
        return [], None

    try:
        random.shuffle(participants)
    except TypeError as e:
        st.error(f"Error shuffling participants: {e}")
        return [], None

    pairs = []
    unmatched = None
    for i in range(0, len(participants), 2):
        if i + 1 < len(participants):
            pairs.append((participants[i], participants[i + 1]))
        else:
            unmatched = participants[i]
    return pairs, unmatched

def get_next_round_name(tournament_id):
    duels = get_duels(tournament_id)
    rounds = [duel["roundName"] for duel in duels if duel["winner"] not in (None, 0)]
    if rounds:
        last_round = max(rounds)
        next_round_number = int(last_round) + 1
        return str(next_round_number)
    else:
        return "1"

def post_rand_match(tournament_id, player1, player2, round_name, winner):
    round_name = get_next_round_name(tournament_id)
    duel = {
        "tournament": {"tournament_id": tournament_id},
        "pid1": player1,
        "pid2": player2,
        "result": {
            "plater1time": 0,
            "plater2time": 0,
        },
        "roundName": round_name,
        "winner": winner
    }
    try:
        headers = get_headers()
        response = requests.post("http://localhost:8080/api/duel", json=duel, headers=headers)
        response.raise_for_status()  # Raise an exception for HTTP errors
        if response.status_code == 200:
            return True
        else:
            st.error(f"Failed to post match: {response.status_code} - {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        st.error(f"Error posting match: {e}")
        return False

def rand_match_afterwards():
    try:
        headers = get_headers()
        response = requests.get("http://localhost:8080/tournament", headers=headers)
        response.raise_for_status()
        tournaments = response.json()
        
        for tournament in tournaments:
            tournament_id = tournament["tournament_id"]
            duels = get_duels(tournament_id)
            if not duels:
                continue
            
            # Find the latest round name
            latest_round = max(duel["roundName"] for duel in duels)
            latest_round_duels = [duel for duel in duels if duel["roundName"] == latest_round]

            winners = [duel["winner"] for duel in latest_round_duels if duel["winner"] not in (None, 0)]

            if len(latest_round_duels) == 1 and latest_round_duels[0]["winner"] not in (None, 0):
                st.write(f"Player {latest_round_duels[0]['winner']} won 1st place in {tournament["name"]}!")            
            elif len(winners) == len(latest_round_duels) and len(winners) > 1:
                for duel in latest_round_duels: st.write(f"{duel["duel_id"]} winner=numofduels")
                pairs, unmatched = randomly_pair_participants(winners)
                next_round_number = int(latest_round) + 1
                next_round_name = str(next_round_number)
                for player1, player2 in pairs:
                    post_rand_match(tournament_id, player1, player2, next_round_name, winner=None)
                
                if unmatched:
                    st.info(f"Unmatched Participant: {unmatched}")
                    # Post the unmatched player as player 1 and set them as the winner
                    post_rand_match(tournament_id, unmatched, None, next_round_name, winner=unmatched)

                    st.info(f"Player {unmatched} has a bye into the next round")
            # else:
            #     for duel in latest_round_duels: st.write(f"{duel["duel_id"]} - {len(winners)} and {len(latest_round_duels)}")
        
    except requests.exceptions.RequestException as e:
        st.error(f"Error: {e}")