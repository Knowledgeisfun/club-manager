package com.vit.club_manager.service;

import com.vit.club_manager.dto.ChannelResponseDTO;
import com.vit.club_manager.model.Channels;
import com.vit.club_manager.repository.ChannelsRepository;
import com.vit.club_manager.security.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final ChannelsRepository channelsRepository;

    public ChannelService(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    // ✅ Extracted your mapping logic into a reusable helper method
    private ChannelResponseDTO mapToDTO(Channels ch) {
        ChannelResponseDTO dto = new ChannelResponseDTO();
        dto.setChannelId(ch.getChannelId());
        dto.setChannelName(ch.getChannelName());
        dto.setChannelType(ch.getChannelType());
        
        // Safely populate teamId and teamName so the frontend can match them dynamically!
        if (ch.getTeam() != null) {
            dto.setTeamId(ch.getTeam().getTeamId());
            dto.setTeamName(ch.getTeam().getTeamName());
        } else {
            dto.setTeamId(null);
            dto.setTeamName(null);
        }
        
        return dto;
    }

    // Keep the original method for potential Admin-only or internal uses
    public List<ChannelResponseDTO> getAllChannels() {
        List<Channels> channels = channelsRepository.findAll();
        return channels.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW: The method your controller needs for Server-Side Filtering!
   public List<ChannelResponseDTO> getChannelsForCurrentUser(CustomUserDetails userDetails) {
        String roleName = userDetails.getUser().getRole().getRoleName().toUpperCase();
        Integer userTeamId = userDetails.getUser().getTeam() != null ? 
                             userDetails.getUser().getTeam().getTeamId() : null;

        // 1. ADMINS SEE EVERYTHING (All Teams & Leadership)
        if (roleName.contains("ADMIN")) {
            return getAllChannels();
        }

        List<Channels> allowedChannels = new ArrayList<>();

        // (REMOVED THE GLOBAL CHANNELS BLOCK ENTIRELY)

        // 2. ONLY LEADS & CO-LEADS GET 'LEADERSHIP' CHANNELS
        if (roleName.contains("LEAD") || roleName.contains("CO_LEAD")) {
            allowedChannels.addAll(channelsRepository.findByChannelTypeIgnoreCase("LEADERSHIP"));
        }

        // 3. USERS GET CHANNELS ASSIGNED TO THEIR SPECIFIC TEAM
        if (userTeamId != null) {
            allowedChannels.addAll(channelsRepository.findByTeam_TeamId(userTeamId));
        }

        return allowedChannels.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}