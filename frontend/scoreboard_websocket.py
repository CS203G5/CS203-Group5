import streamlit as st
import requests
import websocket
import json
import threading
import pandas as pd
import trueskill as ts

env = ts.TrueSkill(draw_probability=0)  # Initialize TrueSkill environment

def get_headers():
    return {"Authorization": f"Bearer {st.session_state['jwt_token']}"}

# Global variable to store duel results
duel_results = []

# Function to handle WebSocket messages
def on_message(ws, message):
    data = json.loads(message)
    duel_results.append(data)  # Update the list of results
    # Refresh the Streamlit app to display the updated list
    st.rerun()

# Initialize WebSocket connection
def connect_ws():
    ws = websocket.WebSocketApp("ws://localhost:8080/ws", on_message=on_message)
    ws.run_forever()

# Function to update duel result
def update_duel_result(did, result_data):
    try: # Add error handling
        headers = get_headers()
        url = f"http://localhost:8080/api/duel/{did}/result"
        response = requests.put(url, json=result_data, headers=headers)
        st.success(F"Duel result updated successfully. {result_data}")
    except requests.exceptions.RequestException as e:
        st.warning({"error": "Failed to update duel result."})

def update_ratings(did, player1_time, player2_time):

    try:
        response = requests.get(f"http://localhost:8080/api/duel/{did}", headers=get_headers())
        duel = response.json() if response.status_code == 200 else []
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching duel info: {e}")
        return []

    try:
        response = requests.get(f"http://localhost:8080/profile/{duel['pid1']}", headers=get_headers())
        player1_profile = response.json() if response.status_code == 200 else []
        st.write(player1_profile)
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching player1 profile: {e}")
        return []
    
    player1_rating = player1_profile['rating']

    try:
        response = requests.get(f"http://localhost:8080/profile/{duel['pid2']}", headers=get_headers())
        player2_profile = response.json() if response.status_code == 200 else []
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching player2 profile: {e}")
        return []
    
    # Check if the rating is 0 and initialize accordingly
    if player1_profile['rating'] == 0:
        player1_rating = env.create_rating()
    else:
        player1_rating = env.Rating(player1_profile['mu'], player1_profile['sigma'])
    
    if player2_profile['rating'] == 0:
        player2_rating = env.create_rating()
    else:
        player2_rating = env.Rating(player2_profile['mu'], player2_profile['sigma'])
    
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
        winner_response = requests.put(f"http://localhost:8080/profile/{winner_id}/rating", params={"newRating": winner_rating.mu}, headers=get_headers())
        st.write(winner_response.json())
    except requests.exceptions.RequestException as e:
        st.error(e)
    try:
        loser_response = requests.put(f"http://localhost:8080/profile/{loser_id}/rating", params={"newRating": loser_rating.mu}, headers=get_headers())
        st.write(loser_response.json())
    except requests.exceptions.RequestException as e:
        st.error(e)

    if winner_response.status_code == 201 and loser_response.status_code == 201:
        st.write(f"Ratings updated successfully for {winner_id} and {loser_id}")
    else:
        st.write(f"Error updating ratings: {winner_response.status_code}, {loser_response.status_code}")

# Function to fetch all duels
def fetch_duels():
    try: # Add error handling
        url = "http://localhost:8080/api/duel"
        response = requests.get(url)
        return response.json() if response.status_code == 200 else []
    except requests.exceptions.RequestException as e:
        return []
    
# Prepare data for display
def prepare_duel_data(duels):
    duel_data = []
    if duels:
        for duel in duels:
            # Convert milliseconds to seconds
            player1_time_s = duel['result']['player1Time'] / 1000 if duel['result'] else "N/A"
            player2_time_s = duel['result']['player2Time'] / 1000 if duel['result'] else "N/A"

            duel_info = {
                "Duel ID": duel['duel_id'],
                "Round": duel['roundName'],
                "Player 1 ID": duel['pid1'],
                "Player 2 ID": duel['pid2'],
                "Player 1 Time (s)": player1_time_s,
                "Player 2 Time (s)": player2_time_s,
                "Winner": f"Player ID {duel['winner']}" if duel['winner'] else "Not determined"
            }
            duel_data.append(duel_info)
    return duel_data

@st.cache_data
def display_duel_table(duel_data):
    if duel_data:
        duel_df = pd.DataFrame(duel_data)
        st.dataframe(duel_df)  # Use st.table() for a static table or st.dataframe() for an interactive one
    else:
        st.write("No duels found.")

def update_scoreboard():
    # Start the WebSocket connection in a separate thread
    if 'ws_thread' not in st.session_state:
        st.session_state.ws_thread = threading.Thread(target=connect_ws, daemon=True)
        st.session_state.ws_thread.start()

    st.title("Duel Management Live Scoreboard")
    for result in duel_results:
        st.write(f"Duel ID: {result['duel_id']}, Player 1 Time: {result['score']['player1Time']} ms, Player 2 Time: {result['score']['player2Time']} ms, Winner: {result['winner']}")

    # Update Duel Form
    did = st.number_input("Enter Duel ID:", min_value=1)
    col1, col2 = st.columns(2)
    with col1:
        player1_seconds = st.number_input("Player 1 Time (seconds):", min_value=0)
    with col2:
        player1_milliseconds = st.number_input("Player 1 Time (milliseconds):", min_value=0)

    col3, col4 = st.columns(2)
    with col3:
        player2_seconds = st.number_input("Player 2 Time (seconds):", min_value=0)
    with col4:
        player2_milliseconds = st.number_input("Player 2 Time (milliseconds):", min_value=0)

    # Convert seconds and milliseconds to total milliseconds
    player1_time = player1_seconds * 1000 + player1_milliseconds
    player2_time = player2_seconds * 1000 + player2_milliseconds

    # Update Duel Result Form
    if st.button("Update Duel Result"):
        result_data = {
            "player1Time": player1_time,
            "player2Time": player2_time,
        }
        update_duel_result(did, result_data)
        update_ratings(did, player1_time, player2_time)

def live_scoreboard():
    st.title("Live Duel Scoreboard")

    if 'duels' not in st.session_state:
        st.session_state.duels = fetch_duels()

    duel_data = prepare_duel_data(st.session_state.duels)

    # Refresh button
    if st.button("Refresh Results"):
        with st.spinner("Fetching latest results..."):
            st.session_state.duels = fetch_duels() 
            duel_data = prepare_duel_data(st.session_state.duels) 

            # Update the cached table
            display_duel_table(duel_data)

            # Check for errors after fetching
            if not st.session_state.duels:
                st.error("Failed to fetch duel results. Please try again.")
