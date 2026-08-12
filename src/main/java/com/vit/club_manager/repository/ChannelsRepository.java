package com.vit.club_manager.repository;

import com.vit.club_manager.model.Channels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelsRepository extends JpaRepository<Channels, Integer> {
    
    // Find a channel by its name (useful for seeders and lookup)
    Optional<Channels> findByChannelName(String channelName);
}