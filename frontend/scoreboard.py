import streamlit as st
import websocket
import json
import random
import stomper
from dotenv import load_dotenv
import os

load_dotenv()
API_URL= os.getenv('API_URL')

def on_message(ws, message):
    score_update = json.loads(message)
    # Update your Streamlit UI with the new score
    st.session_state.scores[score_update['matchId']] = score_update['score']
    st.experimental_rerun()  # Rerun the app to reflect the changes

def on_error(ws, error):
    print(f"Error: {error}")

def on_close(ws):
    print("Connection closed")

def on_open(ws):
    # Optionally send a message when the connection opens
    pass

if __name__ == "__main__":
    st.title("Live Scoreboard")
    
    if 'scores' not in st.session_state:
        st.session_state.scores = {}

    websocket.enableTrace(True)
    # Connecting to websocket
    ws = websocket.create_connection("ws://localhost:8080/ws")

    # Subscribing to topic
    client_id = str(random.randint(0, 1000))
    sub = stomper.subscribe("/topic/scoreboard", client_id, ack='auto')
    ws.send(sub)

    # Sending some message
    score_update = {
        "matchId": "match_123",
        "score": 10
    }
    ws.send(stomper.send("/app/scoreboard/update", score_update))

    while True:
        print("Receiving data: ")
        d = ws.recv()
        print(d)
