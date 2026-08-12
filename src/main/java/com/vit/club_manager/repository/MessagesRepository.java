package com.vit.club_manager.repository;

import com.vit.club_manager.model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Integer> {
    
    // Fetch all messages for a specific channel, sorted chronologically from oldest to newest
    List<Messages> findByChannel_ChannelIdOrderBySentAtAsc(Integer channelId);
}