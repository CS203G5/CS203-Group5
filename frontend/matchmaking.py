import streamlit as st
import requests

API_URL = "http://localhost:8080/api/matchmaking/settings"

def set_matchmaking_settings(tournament_id, matchmaking_type, match_schedule):
    params = {
        "tournamentId": tournament_id,
        "matchmakingType": matchmaking_type,
        "matchSchedule": match_schedule
    }
    response = requests.post(API_URL, params=params)
    if response.status_code == 200:
        st.success("Matchmaking settings saved successfully!")
    else:
        st.error("Failed to save matchmaking settings. Try again.")

def random_matchmaking_with_streaks(tournament_id):
    url = f"{API_URL}/random/streak-based?tournamentId={tournament_id}"
    response = requests.post(url)
    if response.status_code == 200:
        st.success(response.text)
    else:
        st.error("Failed to perform matchmaking.")

def matchmaking_page():
    st.title("Matchmaking Settings")

    tournament_id = st.number_input("Tournament ID", min_value=1)
    matchmaking_type = st.selectbox("Matchmaking Type", ["Random", "Streak-Based"])
    match_schedule = st.text_area("Match Schedule (e.g., Round 1: 12:00, Round 2: 14:00)")

    if st.button("Save Matchmaking Settings"):
        set_matchmaking_settings(tournament_id, matchmaking_type, match_schedule)

    if matchmaking_type == "Streak-Based":
        if st.button("Perform Random Matchmaking (Streak-Based)"):
            random_matchmaking_with_streaks(tournament_id)
