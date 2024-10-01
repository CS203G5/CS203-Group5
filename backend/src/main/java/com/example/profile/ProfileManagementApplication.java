package com.example.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.profile.model"})
@EnableJpaRepositories(basePackages = {"com.example.profile.repository"})
public class ProfileManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileManagementApplication.class, args);
    }
}
