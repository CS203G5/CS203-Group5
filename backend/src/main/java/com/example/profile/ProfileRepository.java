package com.example.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findByUsernameContainingIgnoreCase(String username);
    Optional<Profile> findByUsername(String username);
}
