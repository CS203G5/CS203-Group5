import streamlit as st
import requests
import trueskill as ts
import random
from dotenv import load_dotenv
import os

load_dotenv()
API_URL= os.getenv('API_URL')

DUEL_URL = f"{API_URL}/api/duel"
PARTICIPANT_URL = f"{API_URL}/participants"
PROFILE_URL = f"{API_URL}/profile"
TOURNAMENT_URL = f"{API_URL}/tournament"

# Initialize TrueSkill environment
env = ts.TrueSkill()

def get_headers():
    return {"Authorization": f"Bearer {st.session_state['jwt_token']}"}

def get_duels(tournament_id):
    try:
        headers = get_headers()
        response = requests.get(f"{DUEL_URL}?tid={tournament_id}", headers=headers)
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching duels HERE: {e}")
        return []

def fetch_participants_by_tournament(tournament_id):
    try:
        headers = get_headers()
        response = requests.get(f"{PARTICIPANT_URL}/tournament/{tournament_id}", headers=headers)
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Failed to retrieve participants by tournament: {e}")
        return []

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
    if duels == [] or duels["status"] == 500:
        st.write(duels)
        return "1"
    else:
        rounds = [duel["round_name"] for duel in duels if duel["winner"] not in (None, 0)]
        if rounds:
            last_round = max(rounds)
            next_round_number = int(last_round) + 1
            return str(next_round_number)
        else:
            return "1"

def get_player_profile(player_id):
    if player_id is None or player_id is 0:
        return {}
    else:
        try:
            headers = get_headers()
            response = requests.get(f"{PROFILE_URL}/{player_id}", headers=headers)
            response.raise_for_status()
            if response.status_code == 200:
                return response.json()
            else:
                st.error(f"Failed to get player profile: {response.status_code} - {response.text}")
                return []
        except requests.exceptions.RequestException as e:
            st.error(f"Error getting player profile: {e}")
            return []

def post_matches(tournament_id, player1, player2, round_name, winner):
    profile1 = get_player_profile(player1)
    profile2 = get_player_profile(player2)
    round_name = get_next_round_name(tournament_id)
    duel = {
        "tournament": {
            "tournament_id": tournament_id
        },
        "pid1": profile1,
        "pid2": profile2,
        "round_name": round_name,
        "winner": winner
    }

    try:
        headers = get_headers()
        response = requests.post(f"{DUEL_URL}", json=duel, headers=headers)
        response.raise_for_status()
        # if response.status_code == 201:
        #     return True
        # else:
        #     st.error(f"Failed to post duel: {response.status_code} - {response.text}")
        #     return False
    except requests.exceptions.RequestException as e:
        st.error(e)
        return False

def matchmaking_afterwards():
    try:
        headers = get_headers()
        response = requests.get(f"{TOURNAMENT_URL}", headers=headers)
        response.raise_for_status()
        tournaments = response.json()
        
        for tournament in tournaments:
            tournament_id = tournament["tournament_id"]
            duels = get_duels(tournament_id)
            if not duels:
                continue
            
            # Find the latest round name
            latest_round = max(duel["round_name"] for duel in duels)
            latest_round_duels = [duel for duel in duels if duel["round_name"] == latest_round]

            # winners = [duel["winner"] for duel in latest_round_duels if duel["winner"] not in (None, 0)]
            # st.write(winners)

            # Initialize an empty list to store the winners
            winners = []

            # Iterate over each duel in the latest round duels
            for duel in latest_round_duels:
                # Check the winner of the duel
                if duel["winner"] == 1:
                    # Add pid1 to the winners list if winner is 1
                    winners.append(duel["pid1"])
                elif duel["winner"] == 2:
                    # Add pid2 to the winners list if winner is 2
                    winners.append(duel["pid2"])

            if len(latest_round_duels) == 1 and latest_round_duels[0]["winner"] not in (None, 0):
                # Ending here means the last duel winner is the whole tournament's winner
                st.write()           
            elif len(winners) == len(latest_round_duels) and len(winners) > 1:
                if tournament["is_random"] == 1:
                    pairs, unmatched = randomly_pair_participants(winners)
                else:
                    pairs, unmatched = true_skill_pair_participants(winners)
                next_round_number = int(latest_round) + 1
                next_round_name = str(next_round_number)
                for player1, player2 in pairs:
                    post_matches(tournament_id, player1["profileId"], player2["profileId"], next_round_name, winner=None)
                
                if unmatched:
                    # st.info(f"Unmatched Participant: {unmatched}")
                    post_matches(tournament_id, unmatched["profileId"], None, next_round_name, winner=1)
                    st.info("Player " + unmatched["profileId"] + " has a buy into the next round")
            # else:
            #     for duel in latest_round_duels: st.write(f"{duel["duel_id"]} - {len(winners)} and {len(latest_round_duels)}")
        
    except requests.exceptions.RequestException as e:
        st.error(f"Error: {e}")

def update_ratings(did, player1_time, player2_time):
    env = ts.TrueSkill(draw_probability=0)  # Initialize TrueSkill environment

    try:
        response = requests.get(f"{DUEL_URL}/{did}", headers=get_headers())
        duel = response.json() if response.status_code == 200 else []
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching duel info: {e}")
        return []

    try:
        response = requests.get(f"{PROFILE_URL}/{duel['pid1']['profileId']}", headers=get_headers())
        player1_profile = response.json() if response.status_code == 200 else []
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching player1 profile: {e}")
        return []
    
    player1_rating = player1_profile['rating']

    try:
        response = requests.get(f"{PROFILE_URL}/{duel['pid2']['profileId']}", headers=get_headers())
        player2_profile = response.json() if response.status_code == 200 else []
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching player2 profile: {e}")
        return []
    
    # Check if the rating is 0 and initialize accordingly
    if player1_profile['rating'] == 0:
        player1_rating = env.create_rating(0)
        st.write(player1_rating)
    else:
        player1_rating = env.Rating(player1_profile['rating'], env.sigma)
    
    if player2_profile['rating'] == 0:
        player2_rating = env.create_rating(0)
        st.write(player2_rating)
    else:
        player2_rating = env.Rating(player2_profile['rating'], env.sigma)
    
    # Determine the winner based on lesser time
    if player1_time < player2_time:
        winner_rating, loser_rating = env.rate_1vs1(player1_rating, player2_rating)
        winner_id = duel['pid1']
        loser_id = duel['pid2']
    else:
        winner_rating, loser_rating = env.rate_1vs1(player2_rating, player1_rating)
        winner_id = duel['pid2']
        loser_id = duel['pid1']
    
    try:
        winner_response = requests.put(f"{PROFILE_URL}/{winner_id['profileId']}/rating?newRating={winner_rating.mu}", headers=get_headers())
    except requests.exceptions.RequestException as e:
        st.error(e)
    try:
        loser_response = requests.put(f"{PROFILE_URL}/{loser_id['profileId']}/rating?newRating={loser_rating.mu}", headers=get_headers())
    except requests.exceptions.RequestException as e:
        st.error(e)

    if winner_response.status_code == 201 or 200 and loser_response.status_code == 201 or 200:
        st.write(f"Ratings updated successfully for player {winner_id['profileId']} with rating {winner_rating.mu} and player {loser_id['profileId']} with rating {loser_rating.mu}")
    else:
        st.write(f"Error updating ratings: {winner_response.status_code}, {loser_response.status_code}")

def display_tournament_bracket(tid):
    rounds = {}
    duels = get_duels(tid)
    for duel in duels:
        round_name = duel["round_name"]
        if round_name not in rounds:
            rounds[round_name] = []
        rounds[round_name].append(duel)
    
    st.write("### Matches:")

    for round_name in sorted(rounds.keys(), key=int):
        st.write(f"### Round {round_name}")
        for duel in rounds[round_name]:
            player1 = duel["pid1"]["profileId"]
            winner = duel["winner"]
            if duel["pid2"] in (None, 0):
                st.write(f"Player {player1} gets a buy into the next round")
            else:
                player2 = duel["pid2"]["profileId"]
                st.write(f"Match: Player {player1} vs Player {player2} - Winner: {winner if winner else 'TBD'}")

def true_skill_pair_participants(participants):
    st.write(participants)
    if not participants:
        st.error("Participants list is empty or not initialized.")
        return [], None

    # Create a TrueSkill environment
    env = ts.TrueSkill()

    # Create a list of (participant, rating) tuples by getting the rating from get_player_profile
    rated_participants = []
    for par in participants:
        # profile = get_player_profile(par["profile"]["profileId"])
        profile = get_player_profile(par["profileId"])
        if profile and "rating" in profile:
            rating = env.create_rating(profile["rating"])
            rated_participants.append((par, rating))
        else:
            # st.error(f"Could not get rating for participant {par["profile"]['profileId']}")
            st.error(f"Could not get rating for participant {par['profileId']}")
            return [], None

    # Sort participants by their rating
    rated_participants.sort(key=lambda x: x[1].mu, reverse=True)

    pairs = []
    unmatched = None
    for i in range(0, len(rated_participants), 2):
        if i + 1 < len(rated_participants):
            pairs.append((rated_participants[i][0], rated_participants[i + 1][0]))
        else:
            unmatched = rated_participants[i][0]
    return pairs, unmatched