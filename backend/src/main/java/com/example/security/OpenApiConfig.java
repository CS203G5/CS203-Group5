package com.example.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Speed Climber API")
                .version("1.0")
                .description("API Documentation"))
            .components(new Components()
                .addSecuritySchemes("cognito-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer") 
                    .bearerFormat("JWT")
                    .description("Please enter JWT token")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization")))
            .addSecurityItem(new SecurityRequirement().addList("cognito-jwt"));
    }
}