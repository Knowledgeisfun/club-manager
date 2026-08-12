package com.vit.club_manager.service;

import com.vit.club_manager.dto.ChannelResponseDTO;
import com.vit.club_manager.model.Channels;
import com.vit.club_manager.repository.ChannelsRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelService {

    private final ChannelsRepository channelsRepository;

    public ChannelService(ChannelsRepository channelsRepository) {
        this.channelsRepository = channelsRepository;
    }

    public List<ChannelResponseDTO> getAllChannels() {
        List<Channels> channels = channelsRepository.findAll();
        return channels.stream().map(ch -> {
            ChannelResponseDTO dto = new ChannelResponseDTO();
            dto.setChannelId(ch.getChannelId());
            dto.setChannelName(ch.getChannelName());
            dto.setChannelType(ch.getChannelType());
            
            // ✅ Safely populate teamId and teamName so the frontend can match them dynamically!
            if (ch.getTeam() != null) {
                dto.setTeamId(ch.getTeam().getTeamId());
                dto.setTeamName(ch.getTeam().getTeamName());
            } else {
                dto.setTeamId(null);
                dto.setTeamName(null);
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
}