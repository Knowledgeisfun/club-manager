package com.vit.club_manager.repository;

import com.vit.club_manager.model.Teams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, Integer> {
    boolean existsByTeamName(String teamName);
    Teams findByTeamName(String teamName);
}