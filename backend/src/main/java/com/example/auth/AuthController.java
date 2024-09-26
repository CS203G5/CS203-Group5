package com.example.auth;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    // Optional - Placeholder if you want to keep them
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> user) {
        // Streamlit should handle direct registration via Cognito
        return "Register through frontend";
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials) {
        // Streamlit should handle direct login via Cognito
        return "Login through frontend";
    }

    // Protected endpoint that requires JWT authentication
    @GetMapping("/protected")
    public ResponseEntity<String> getProtected() {
        // This endpoint will only be accessible if the JWT token is valid
        return ResponseEntity.ok("Access granted. You are authenticated!");
    }
}