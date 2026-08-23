package com.vit.club_manager.repository;

import com.vit.club_manager.model.Teams;
import com.vit.club_manager.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;


@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    
    // Checks if a username is already taken when registering
    boolean existsByEmail(String email);
    
    boolean existsByRegistrationNumber(String registrationNumber);

    Optional<Users> findByEmail(String email);

    List<Users> findByTeam(Teams team);

}