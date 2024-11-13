package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] ADMIN_MATCHERS = {
        "/tournament/?",
        "/tournament/**",
        "/duel/?",
        "/duel/**"
    };
    
    private static final String[] PERMIT_ALL_GETTERS = {
        "/tournament", 
        "/tournament/organizer", 
        "/tournament/search", 
        "/tournament/filter", 
        "/tournament/sorted", 
        "/tournament/matching", 
        "/duel", 
        "/duel/player/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity in REST APIs
            .authorizeHttpRequests(auth -> auth
                // Public endpoints that don't require authentication
                .requestMatchers(HttpMethod.POST, "/profile").permitAll()
                .requestMatchers("/swagger-ui/**", 
                               "/swagger-ui.html", 
                               "/v3/api-docs/**", 
                               "/api-docs/**").permitAll()
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                // Allow GET requests to public endpoints
                .requestMatchers(HttpMethod.GET, PERMIT_ALL_GETTERS).permitAll()
                // Require authentication for admin endpoints
                .requestMatchers(ADMIN_MATCHERS).authenticated()
                .requestMatchers(ADMIN_MATCHERS).hasRole("ADMIN")
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }
}