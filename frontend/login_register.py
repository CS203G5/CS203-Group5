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