package com.example.profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Profile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long profileId;
    
    @NotNull
    @Size(min = 3, max = 50)
    private String username;
    
    @NotNull
    @Email
    private String email;
    
    @Size(max = 255)
    private String bio;
    
    @NotNull
    @Column(name = "privacy_settings")
    private String privacy_settings;

    @Column(name = "rating", nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double rating = 0.0;

    @Column(name = "role")
    @NotNull(message = "Authorities should not be null")
    private String role;

    public String getPrivacySettings() {
        return this.privacy_settings;
    }
    public void setPrivacySettings(String privacy_settings) {
        this.privacy_settings = privacy_settings;
    }
}
