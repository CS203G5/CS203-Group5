import streamlit as st
import boto3
from botocore.exceptions import ClientError
import os
from dotenv import load_dotenv

# Load environment variables from .env file
load_dotenv()
COGNITO_CLIENT_ID = os.getenv('AWS_COGNITO_CLIENT_ID')

# Set up AWS Cognito client
client = boto3.client('cognito-idp', region_name='us-east-1')

# Function to log in user with AWS Cognito
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
            token = response['AuthenticationResult']['IdToken']
            st.session_state['jwt_token'] = token
            st.success('Login successful! JWT Token received.')
        except ClientError as e:
            st.error(f"Login failed: {e.response['Error']['Message']}")

# Function to register user with AWS Cognito
def register_user():
    st.title('Register')

    name = st.text_input('Name')
    username = st.text_input('Username')
    email = st.text_input('Email')
    password = st.text_input('Password', type='password')
    gender = st.text_input('Gender')  
    phone_number = st.text_input('Phone Number', value='+65') 

    if st.button('Register'):
        try:
            response = client.sign_up(
                ClientId=COGNITO_CLIENT_ID, 
                Username=username,
                Password=password,
                UserAttributes=[
                    {'Name': 'email', 'Value': email},
                    {'Name': 'gender', 'Value': gender},  
                    {'Name': 'phone_number', 'Value': phone_number},  
                    {'Name': 'name', 'Value': name}, 
                ]
            )
            st.success("Registration successful! Please check your email for verification.")
            st.session_state['username'] = username  # Save the username to session state
        except ClientError as e:
            st.error(f"Registration failed: {e.response['Error']['Message']}")

# Function to confirm user registration (using the verification code sent to email)
def confirm_registration():
    st.title('Confirm Registration')

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
        except ClientError as e:
            st.error(f"Verification failed: {e.response['Error']['Message']}")