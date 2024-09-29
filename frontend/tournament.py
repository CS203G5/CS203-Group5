import pandas as pd
import datetime
import streamlit as st
import requests

st.set_page_config(layout="wide")

# Mock logged in user
def get_logged_in_user():
    return {"name": "John Doe", "id": 3}
logged_in_user = get_logged_in_user()


# Session state for the toggle functionality
if "show_create_form" not in st.session_state:
    st.session_state["show_create_form"] = False
if "show_update_form" not in st.session_state:
    st.session_state["show_update_form"] = False
    st.session_state["selected_tournament_id"] = None

def toggle_create_tournament():
    st.session_state["show_create_form"] = not st.session_state["show_create_form"]

def toggle_update_tournament(tournament_id):
    st.session_state["show_update_form"] = not st.session_state["show_update_form"]
    st.session_state["selected_tournament_id"] = tournament_id


# API functions
def fetch_tournaments_by_admin(organizer_id):
    try:
        url = f"http://localhost:8080/tournament/organizer/{organizer_id}"
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching data")
        return []
    
def fetch_tournament(tournament_id):
    try:
        url = f"http://localhost:8080/tournament/{tournament_id}"
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching tournament data")
        return None
    
def create_tournament(payload):
    try:
        response = requests.post(f"http://localhost:8080/tournament", json=payload)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error("Failed to create tournament.")
        return None

def update_tournament(tournament_id, payload):
    try:
        response = requests.put(f"http://localhost:8080/tournament/{tournament_id}", json=payload)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error("Failed to update tournament.")
        return None

def delete_tournaments(tournament_ids):
    try:
        response = requests.delete(f"http://localhost:8080/tournament", json=tournament_ids)
        if response.status_code == 200:
            return True
    except requests.exceptions.RequestException as e:
        st.error(f"Failed to delete tournaments: {e}")
        return None



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
                "isRandom": is_random,
                "date": date.strftime("%Y-%m-%d"),
                "time": time.strftime("%H:%M:%S"),
                "location": location,
                "organizer_id": logged_in_user["id"],
                "description": description
            }

            if create_tournament(payload):
                st.success("Tournament created successfully!")


search_term = st.text_input("🔍 Enter tournament name or keyword", "", placeholder="Type to search...", label_visibility="collapsed")
st.markdown("---")

tournament_data = fetch_tournaments_by_admin(organizer_id=logged_in_user["id"])
if tournament_data:
    df = pd.DataFrame(
        tournament_data, 
        columns=["select", "tournament_id", "name", "date", "time", "location", "description", "isRandom", "modifiedAt"],
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
            "isRandom": st.column_config.CheckboxColumn(
                "Randomized",
                disabled=True,
                width="small"
            ),
            "modifiedAt": st.column_config.DatetimeColumn(
                "Last Modified",
                disabled=True,
                width="small",
                help="DateTime in YYYY-MM-DD, HH:MM:SS format"
            ),
        },
        disabled=["name", "isRandom", "date", "location", "description", "modifiedAt"],  # Disable all columns except 'select'
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
        if st.button("Edit Selected Tournament"):
            selected_tournament_id = selected_tournaments[0]
            tournament_data = fetch_tournament(selected_tournament_id)
            if tournament_data:
                toggle_update_tournament(selected_tournament_id)
                st.session_state["tournament_data"] = tournament_data

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
        is_random = st.checkbox("Randomized Matching", value=tournament_data["isRandom"])
        description = st.text_area("Description", tournament_data["description"])
        location = st.text_input("Location", tournament_data["location"])
        date = st.date_input("Date", value=datetime.datetime.strptime(tournament_data["date"], "%Y-%m-%d").date())
        time = st.time_input("Time", value=datetime.datetime.strptime(tournament_data["time"], "%H:%M:%S").time())
        submitted = st.form_submit_button("Update Tournament")

        if submitted:
            payload = {
                "name": name,
                "isRandom": is_random,
                "date": date.strftime("%Y-%m-%d"),
                "time": time.strftime("%H:%M:%S"),
                "location": location,
                "description": description,
                "organizer_id": logged_in_user["id"],
            }

            if update_tournament(tournament_data["tournament_id"], payload):
                st.success("Tournament updated successfully! Please refresh to see the changes.")

