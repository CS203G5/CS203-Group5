import streamlit as st
import requests
import pandas as pd

# Fetch ongoing tournaments from the backend
def fetch_ongoing_tournaments():
    try:
        response = requests.get("http://localhost:8080/tournaments/ongoing") 
        if response.status_code == 200:
            return response.json()  # Returns a list of ongoing tournaments
        else:
            st.error("Failed to fetch ongoing tournaments.")
            return []
    except Exception as e:
        st.error(f"Error fetching ongoing tournaments: {e}")
        return []

# Function to display scoreboard for a specific tournament
def show_scoreboard(tournament):
    st.title(f"Scoreboard for {tournament['name']}")
    
    # Fetch players and their scores (mocking the players for now)
    players = tournament.get("players", [])
    if players:
        player_data = pd.DataFrame(players)
        player_data = player_data.sort_values(by=['score'], ascending=False)
        player_data['rank'] = range(1, len(player_data) + 1)
        st.table(player_data[['rank', 'name', 'wins', 'losses', 'score']])
    else:
        st.write("No player data available for this tournament.")

# Main function to allow user to select an ongoing tournament and view the scoreboard
def scoreboard_page():
    st.title("Ongoing Tournaments")

    # Fetch the list of ongoing tournaments
    tournaments = fetch_ongoing_tournaments()

    if not tournaments:
        st.write("No ongoing tournaments available.")
        return

    # Tournament selection
    tournament_options = {t["name"]: t for t in tournaments}
    selected_tournament_name = st.selectbox("Select Tournament", list(tournament_options.keys()))

    # Show the scoreboard for the selected tournament
    if selected_tournament_name:
        selected_tournament = tournament_options[selected_tournament_name]
        show_scoreboard(selected_tournament)

