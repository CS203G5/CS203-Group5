package com.example.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.ExternalDocumentation;
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
                .description("API documentation for the Speed Climber application")
                .contact(new Contact()
                    .name("CS203-Group5")
                    .url("https://github.com/CS203G5/CS203-Group5"))
            .license(new License()
                .name("MIT License")));
    }
}
