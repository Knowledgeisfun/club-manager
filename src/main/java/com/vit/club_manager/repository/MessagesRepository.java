package com.vit.club_manager.repository;

import com.vit.club_manager.model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Integer> {
    
    // This allows you to easily fetch all messages for a specific team!
    List<Messages> findByTeam_TeamId(Integer teamId);
}