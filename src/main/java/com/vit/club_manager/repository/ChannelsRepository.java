package com.vit.club_manager.repository;

import com.vit.club_manager.model.Channels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelsRepository extends JpaRepository<Channels, Integer> {
    
    // Find a channel by its name (useful for seeders and lookup)
    Optional<Channels> findByChannelName(String channelName);

    // NEW: Fetch channels by their type (e.g., "GLOBAL" or "LEADERSHIP")
    List<Channels> findByChannelTypeIgnoreCase(String channelType);

    // NEW: Fetch channels that belong to a specific team
    List<Channels> findByTeam_TeamId(Integer teamId);
}