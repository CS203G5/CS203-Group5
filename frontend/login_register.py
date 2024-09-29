import streamlit as st
import boto3
from botocore.exceptions import ClientError
import os
from dotenv import load_dotenv

load_dotenv()
COGNITO_CLIENT_ID = os.getenv('AWS_COGNITO_CLIENT_ID')
COGNITO_USER_POOL_ID = os.getenv('AWS_COGNITO_USER_POOL_ID') 
AWS_REGION = os.getenv('AWS_REGION')

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
                    except ClientError as e:
                        st.error(f"MFA verification failed: {e.response['Error']['Message']}")
            else:
                # No MFA, regular login
                token = response['AuthenticationResult']['IdToken']
                st.session_state['jwt_token'] = token
                st.success('Login successful.')

        except ClientError as e:
            st.error(f"Login failed: {e.response['Error']['Message']}")


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