import pandas as pd
import datetime
import streamlit as st
import requests
import random
import trueskill as ts
from algorithms import *
from scoreboard_websocket import live_scoreboard
from dotenv import load_dotenv
import os

load_dotenv()
API_URL= os.getenv('API_URL')
TOURNAMENT_URL = f"{API_URL}/tournament"
PARTICIPANT_URL = f"{API_URL}/participant"
DUEL_API = f"{API_URL}/match"

if "show_create_form" not in st.session_state:
    st.session_state["show_create_form"] = False
if "show_update_form" not in st.session_state:
    st.session_state["show_update_form"] = False
if "selected_tournament_id" not in st.session_state:
    st.session_state["selected_tournament_id"] = None
if "jwt_token" not in st.session_state:
    st.session_state["jwt_token"] = None  # Initialize jwt_token

# Mock logged in user
# def get_logged_in_user():
#     return {"name": "John Doe", "id": 1}
# logged_in_user = get_logged_in_user()

def get_headers():
    return {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    
    
def toggle_create_tournament():
    st.session_state["show_create_form"] = not st.session_state["show_create_form"]

def toggle_update_tournament(tournament_id):
    st.session_state["show_update_form"] = not st.session_state["show_update_form"]
    st.session_state["selected_tournament_id"] = tournament_id


# API functions
def fetch_tournaments_by_admin(organizer_id):
    try:
        url = f"{TOURNAMENT_URL}/organizer/{organizer_id}"
        headers = get_headers()
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching data: {e}")
        return []
    
def fetch_tournament(tournament_id):
    try:
        url = f"{TOURNAMENT_URL}/{tournament_id}"   
        headers = get_headers()
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching tournament data")
        return None
    
def create_tournament(payload):
    try:
        headers = get_headers()
        response = requests.post(f"{TOURNAMENT_URL}", json=payload, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error("Failed to create tournament.")
        return None

def update_tournament(tournament_id, payload):
    try:
        headers = get_headers()
        response = requests.put(f"{TOURNAMENT_URL}/{tournament_id}", json=payload, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error("Failed to update tournament.")
        return None

def delete_tournaments(tournament_ids):
    try:
        headers = get_headers()
        response = requests.delete(f"{TOURNAMENT_URL}", json=tournament_ids, headers=headers)
        if response.status_code == 200:
            return True
    except requests.exceptions.RequestException as e:
        st.error(f"Failed to delete tournaments: {e}")
        return None

def tournament_page():
    st.header("My Tournaments")
    # Create Tournament button and form
    create_tournament_btn = st.button(
        "Create Tournament", 
        key="create_tournament_btn", 
        help="Click to create a new tournament", 
        on_click=toggle_create_tournament
    )

    if st.session_state["show_create_form"]:
        with st.form("create_tournament_form"):
            st.subheader("Create a New Tournament")
            name = st.text_input("Tournament Name", "")
            is_random = st.checkbox("Randomized Matching")
            description = st.text_area("Description")
            location = st.text_input("Location", "")
            date = st.date_input("Date", value=datetime.datetime.now())
            time = st.time_input("Time", value=datetime.datetime.now().time())
            submitted = st.form_submit_button("Create Tournament")

            if submitted:
                payload = {
                    "name": name,
                    "is_random": is_random,
                    "date": date.strftime("%Y-%m-%d"),
                    "time": time.strftime("%H:%M:%S"),
                    "location": location,
                    "organizer_id": st.session_state["profile_id"],
                    "description": description
                }

                if create_tournament(payload):
                    st.success("Tournament created successfully!")


    search_term = st.text_input("🔍 Enter tournament name or keyword", "", placeholder="Type to search...", label_visibility="collapsed")
    st.markdown("---")

    tournament_data = fetch_tournaments_by_admin(organizer_id= st.session_state["profile_id"])
    if tournament_data:
        df = pd.DataFrame(
            tournament_data, 
            columns=["select", "tournament_id", "name", "date", "time", "location", "description", "is_random", "modified_at"],
        )
        
        # 'Select' column to the DataFrame
        if "select" not in df.columns:
            df["select"] = False

        if "select_all" not in st.session_state:
            st.session_state["select_all"] = False

        select_all = st.checkbox("Select All", key="select_all", value=st.session_state.select_all)


        # Update the checkbox status for all rows based on "Select All"
        if select_all:
            df["select"] = True
        else:
            df["select"] = False

        # Filter the DataFrame based on the search term
        if search_term:
            df = df[df['name'].str.contains(search_term, case=False, na=False)]

        # Display the tournaments in a table
        edited_df = st.data_editor(
            df,
            column_config={
                "select": st.column_config.CheckboxColumn(
                    "Select",
                    help="Select tournament for deletion",
                    default=False,
                    width="small"
                ),
                "tournament_id": st.column_config.TextColumn(
                    "Tournament ID",
                    disabled=True,
                    width="small"
                ),
                "name": st.column_config.TextColumn(
                    "Tournament Name",
                    disabled=True,
                    width="medium"
                ),
                "location": st.column_config.TextColumn(
                    "Location",
                    disabled=True,
                    width="medium"
                ),
                "date": st.column_config.TextColumn(
                    "Date",
                    disabled=True,
                    width="small",
                    help="Date in YYYY-MM-DD format"
                ),
                "time": st.column_config.TextColumn(
                    "Time",
                    disabled=True,
                    width="small",
                    help="HH:MM format"
                ),
                "description": st.column_config.TextColumn(
                    "Description",
                    disabled=True,
                    width="large"
                ),
                "is_random": st.column_config.CheckboxColumn(
                    "Randomized",
                    disabled=True,
                    width="small"
                ),
                "modified_at": st.column_config.DatetimeColumn(
                    "Last Modified",
                    disabled=True,
                    width="medium",
                    help="DateTime in YYYY-MM-DD, HH:MM:SS format"
                ),
            },
            disabled=["name", "is_random", "date", "location", "description", "modified_at"],  # Disable all columns except 'select'
            hide_index=True,
            use_container_width=True,
            height=df.shape[0] * 30 + 100
        )

        # Update the DataFrame with the selected checkboxes
        df["select"] = edited_df["select"]

        # Count selected tournaments
        selected_tournaments = df[df["select"] == True]["tournament_id"].tolist()
        selected_count = len(selected_tournaments)

        # Edit button
        if selected_count == 1:
            # Randomize matches button
            if st.button("Match Participants"):
                selected_tournament_id = selected_tournaments[0]
                tournament_data = fetch_tournament(selected_tournament_id)
                if tournament_data:
                    # Fetch duels to check if the tournament ID is already in duels
                    headers = get_headers()
                    response = requests.get(f"{DUEL_API}?tid={selected_tournament_id}", headers=headers)
                    
                    #DEBUG
                    st.write(response.status_code)

                    if response.status_code == 200 or response.status_code == 404:
                        st.write("Matching was done, no more matching can be done.")
                    else:
                        participants = fetch_participants_by_tournament(selected_tournament_id)
                        if tournament_data["is_random"]:
                            st.write("Randomizing matches...")
                            if not participants:
                                st.write("No participants registered to match.")
                            else:
                                pairs, unmatched = randomly_pair_participants(participants)
                                
                                st.write(f"Tournament ID: {selected_tournament_id}")
                                
                                for pair in pairs:
                                    player1 = pair[0]['profile']['profileId']
                                    player2 = pair[1]['profile']['profileId']
                                    if post_matches(selected_tournament_id, player1, player2, round_name=1, winner=0):
                                        st.write(f"Match: Player {player1} vs Player {player2} - Matched Successfully")
                                    # else:
                                    #     st.write(f"Match: Player {player1} vs Player {player2} - Error Matching")
                                
                                if unmatched:
                                    st.write(f"Unmatched Participant: {unmatched['profile']['profileId']}")
                                    # Post the unmatched player as player 1 and set them as the winner
                                    if post_matches(selected_tournament_id, unmatched['profile']['profileId'], player2=0, round_name=1, winner=unmatched['profile']['profileId']):
                                        st.write(f"Player {unmatched['profile']['profileId']} has a buy into the next round")
                                    # else:
                                    #     st.error(f"Error matching Player {unmatched['profile']['profileId']}")
                        else:
                            # Use TrueSkill for matching
                            st.write("Using TrueSkill for matching...")
                            if not participants:
                                st.write("No participants registered to match.")
                            else:
                                profiles = [p["profile"] for p in participants]
                                pairs, unmatched = true_skill_pair_participants(profiles)
                                
                                st.write(f"Tournament ID: {selected_tournament_id}")
                                
                                for pair in pairs:
                                    player1 = pair[0]['profileId']
                                    player2 = pair[1]['profileId']
                                    if post_matches(selected_tournament_id, player1, player2, round_name=1, winner=0):
                                        st.write(f"Match: Player {player1} vs Player {player2} - Matched Successfully")
                                    # else:
                                    #     st.write(f"Match: Player {player1} vs Player {player2} - Error Matching")
                                
                                if unmatched:
                                    st.write(f"Unmatched Participant: {unmatched['profileId']}")
                                    # Post the unmatched player as player 1 and set them as the winner
                                    if post_matches(selected_tournament_id, unmatched['profileId'], player2=0, round_name=1, winner=unmatched['profileId']):
                                        st.write(f"Player {unmatched['profileId']} has a buy into the next round")
                                    # else:
                                    #     st.error(f"Error matching Player {unmatched['profileId']}")
                    display_tournament_bracket(selected_tournament_id)

            if st.button("Edit Selected Tournament"):
                selected_tournament_id = selected_tournaments[0]
                tournament_data = fetch_tournament(selected_tournament_id)
                if tournament_data:
                    toggle_update_tournament(selected_tournament_id)
                    st.session_state["tournament_data"] = tournament_data

            if st.button("Go to scoreboard"):
                live_scoreboard(selected_tournaments[0])

        # Delete button
        if selected_count > 0 and not st.session_state["show_update_form"]:
            if st.button("Delete Selected Tournaments", type="primary"):
                deleted = delete_tournaments(selected_tournaments)
                if deleted:
                    st.success("Selected tournaments deleted successfully! Please refresh to see the changes.")

    else:
        st.write("No tournaments found")
        

    # Show or hide the update form based on the toggle state
    if st.session_state["show_update_form"]:
        tournament_data = st.session_state["tournament_data"]
        with st.form("update_tournament_form"):
            st.subheader("Update Tournament")
            name = st.text_input("Tournament Name", tournament_data["name"])
            is_random = st.checkbox("Randomized Matching", value=tournament_data["is_random"])
            description = st.text_area("Description", tournament_data["description"])
            location = st.text_input("Location", tournament_data["location"])
            date = st.date_input("Date", value=datetime.datetime.strptime(tournament_data["date"], "%Y-%m-%d").date())
            time = st.time_input("Time", value=datetime.datetime.strptime(tournament_data["time"], "%H:%M:%S").time())
            submitted = st.form_submit_button("Update Tournament")

            if submitted:
                payload = {
                    "name": name,
                    "is_random": is_random,
                    "date": date.strftime("%Y-%m-%d"),
                    "time": time.strftime("%H:%M:%S"),
                    "location": location,
                    "description": description,
                    "organizer_id": st.session_state["profile_id"],
                }

                if update_tournament(tournament_data["tournament_id"], payload):
                    st.success("Tournament updated successfully! Please refresh to see the changes.")