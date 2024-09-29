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
                        st.success('Login successful with MFA! JWT Token received.')
                    except ClientError as e:
                        st.error(f"MFA verification failed: {e.response['Error']['Message']}")
            else:
                # No MFA, regular login
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
    country = st.text_input('Country') 

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
            st.session_state['registered'] = True  # Flag to indicate that the user just registered
        except ClientError as e:
            st.error(f"Registration failed: {e.response['Error']['Message']}")

    # Show confirmation form if registration is successful
    if st.session_state.get('registered', False):
        confirm_registration()  # Display confirmation form right after registration

# Function to confirm user registration (using the verification code sent to email)
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
            st.session_state['registered'] = False  # Reset the registration flag
        except ClientError as e:
            st.error(f"Verification failed: {e.response['Error']['Message']}")
