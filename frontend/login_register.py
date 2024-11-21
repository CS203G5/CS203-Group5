import streamlit as st
import os, boto3, requests, pymysql
from botocore.exceptions import ClientError
import os
from dotenv import load_dotenv
import os

load_dotenv()
COGNITO_CLIENT_ID = os.getenv('AWS_COGNITO_CLIENT_ID')
COGNITO_USER_POOL_ID = os.getenv('AWS_COGNITO_USER_POOL_ID') 
AWS_REGION = os.getenv('AWS_REGION')
API_URL= os.getenv('API_URL')
PROFILE_URL = f"{API_URL}/profile"

# Set up AWS Cognito client
client = boto3.client('cognito-idp', region_name=AWS_REGION)


def login_user():
    st.title('Login')

    username = st.text_input('Username')
    password = st.text_input('Password', type='password')

    if st.button('Login'):
        try:
            response = client.initiate_auth(
                ClientId=COGNITO_CLIENT_ID, 
                AuthFlow='USER_PASSWORD_AUTH',
                AuthParameters={
                    'USERNAME': username,
                    'PASSWORD': password,
                }
            )

            if 'AuthenticationResult' in response:
                token = response['AuthenticationResult']['IdToken']
                st.session_state['jwt_token'] = token
                st.session_state['username'] = username
                st.success('Login successful.')
                
                # Fetch user's profile information
                profile_info = fetch_profile_by_username(username)
                if profile_info:
                    st.session_state['profile_id'] = profile_info['profileId']
                    st.session_state['role'] = profile_info['role']
                    st.success(f"Welcome, {profile_info['username']}!")
                else:
                    st.warning("Profile not found. Please create a profile.")

                make_authenticated_request()

            # MFA login
            if 'ChallengeName' in response and response['ChallengeName'] == 'SES_MFA':
                st.session_state['username'] = username
                st.session_state['session'] = response['Session']
                st.info('MFA code required. Please enter the code sent to your email.')

                mfa_code = st.text_input('MFA Code')
                if st.button('Submit MFA Code'):
                    try:
                        mfa_response = client.respond_to_auth_challenge(
                            ClientId=COGNITO_CLIENT_ID,
                            ChallengeName='SES_MFA',
                            Session=st.session_state['session'],
                            ChallengeResponses={
                                'USERNAME': username,
                                'SES_MFA_CODE': mfa_code
                            }
                        )
                        token = mfa_response['AuthenticationResult']['IdToken']
                        st.session_state['jwt_token'] = token
                        st.success('Login with MFA successful.')

                        make_authenticated_request()

                    except ClientError as e:
                        st.error(f"MFA verification failed: {e.response['Error']['Message']}")
            else:
                # No MFA, regular login
                token = response['AuthenticationResult']['IdToken']
                st.session_state['jwt_token'] = token
                st.session_state['username'] = username
                st.success('Login successful.')
                
                make_authenticated_request()

        except ClientError as e:
            st.error(f"Login failed: {e.response['Error']['Message']}")

def get_headers():
    if 'jwt_token' in st.session_state:
        return {"Authorization": f"Bearer {st.session_state['jwt_token']}"}
    else:
        st.error("JWT token not found in session state.")
        return {}

def fetch_profile_by_username(username):
    try:
        headers = get_headers()
        response = requests.get(f"{PROFILE_URL}/by-username/{username}", headers=headers)
        if response.status_code == 200:
            return response.json()
        else:
            st.error(f"Failed to fetch profile: {response.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching profile: {e}")
        return None

def register_profile(username, email):
    try:
        headers = get_headers()

        # Define the payload with the profile information to be sent in the POST request
        payload = {
            "username": username,
            "email": email,
            "bio": "",
            "privacySettings": "Public",
            "rating": 0.0,
            "role": "PLAYER" # Set the role to "PLAYER" by default
        }

        # Send a POST request with the profile data
        response = requests.post(f"{PROFILE_URL}", json=payload, headers=headers)

        # Check if the response was successful (status code 201 or 200)
        if response.status_code == 200:
            # If successful, return the response data (profile data in JSON format)
            st.success("Profile created successfully!")
            return response.json()
        else:
            # If the request failed (non-200 status code), log the error and return None
            st.error(f"Failed to create profile: {response.status_code} - {response.text}")
            return None
    except requests.exceptions.RequestException as e:
        # Handle request exceptions (e.g., network issues, timeout, etc.)
        st.error(f"Error creating profile: {e}")
        return None

    
def register_user():
    st.title('Register')

    country_options = [
        'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany', 'France', 'Singapore',
        'India', 'China', 'Japan', 'Brazil', 'Mexico', 'South Africa', 'Netherlands', 'Italy',
        'Spain', 'Sweden', 'Norway', 'Russia', 'South Korea', 'New Zealand'
    ]

    col1, col2 = st.columns(2)
    with col1:
        name = st.text_input('Name')
        email = st.text_input('Email')
        gender = st.selectbox('Gender', ['Male', 'Female'])  

    with col2:
        username = st.text_input('Username') 
        password = st.text_input('Password', type='password')
        country = st.selectbox('Country', country_options) 

    if st.button('Register'):
        try:
            response = client.sign_up(
                ClientId=COGNITO_CLIENT_ID, 
                Username=username,  
                Password=password,
                UserAttributes=[
                    {'Name': 'email', 'Value': email},
                    {'Name': 'gender', 'Value': gender},  
                    {'Name': 'name', 'Value': name},
                    {'Name': 'custom:country', 'Value': country} 
                ]
            )
            register_profile(username, email)
            st.success("Registration successful! Please check your email for verification.")
            st.session_state['username'] = username
            st.session_state['registered'] = True  
        except ClientError as e:
            st.error(f"Registration failed: {e.response['Error']['Message']}")

    if st.session_state.get('registered', False):
        confirm_registration()

def confirm_registration():
    if 'username' in st.session_state:
        username = st.session_state['username']
    else:
        st.error("No user to confirm. Please register first.")
        return

    verification_code = st.text_input('Enter verification code sent to your email')

    if st.button('Confirm Registration'):
        try:
            response = client.confirm_sign_up(
                ClientId=COGNITO_CLIENT_ID,
                Username=username,
                ConfirmationCode=verification_code,
            )
            st.success("Email verification successful! You can now log in.")
            st.session_state['registered'] = False 
        except ClientError as e:
            st.error(f"Verification failed: {e.response['Error']['Message']}")

def make_authenticated_request():
    if 'jwt_token' in st.session_state:
        headers = {
            'Authorization': f"Bearer {st.session_state['jwt_token']}"
        }
        response = requests.get(f'{API_URL}/tournament', headers=headers)

        # DEBUG REMOVE
        st.write(st.session_state['jwt_token'])
        
        # DEBUG - REMOVE
        # if response.status_code == 200:
        #     st.write(response.json())  # Display the JSON response in the Streamlit app
        # else:
        #     st.error(f"Request failed with status code: {response.status_code}")
    else:
        st.warning('You must log in first to make an authenticated request.')