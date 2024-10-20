package com.example.integration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
// import io.github.cdimascio.dotenv.Dotenv;

import java.util.Map;

public class CognitoAuthUtils {

    // private static final Dotenv dotenv    = Dotenv.load();

    private static final String CLIENT_ID = "5cf7ptk9ikt0brqh3ongvlaair";
    private static final String USER_POOL_ID = "us-east-1_ZbMFSqvjF";
    private static final Region REGION = Region.US_EAST_1;

    public static String getJwtToken(String username, String password) {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(REGION)
                .build();

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(CLIENT_ID)
                .authParameters(Map.of("USERNAME", username, "PASSWORD", password))
                .build();

        InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);

        return authResponse.authenticationResult().idToken(); // This is your JWT token
    }

    // Main method to run the authentication and print the token
    public static void main(String[] args) {
        String username = "khairyo"; // hardcoded username
        String password = "Hello12."; // hardcoded password

        // Call the getJwtToken method and print the token
        String token = getJwtToken(username, password);
        System.out.println("Generated JWT Token: " + token);
    }
}
