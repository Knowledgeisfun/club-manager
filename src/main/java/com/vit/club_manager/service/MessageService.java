package com.vit.club_manager.service;

import com.vit.club_manager.config.AppConstants;
import com.vit.club_manager.dto.ChannelResponseDTO;
import com.vit.club_manager.dto.MessageResponseDTO;
import com.vit.club_manager.model.Channels;
import com.vit.club_manager.model.Messages;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.exception.ResourceNotFoundException;
import com.vit.club_manager.repository.ChannelsRepository;
import com.vit.club_manager.repository.MessagesRepository;
import com.vit.club_manager.repository.UsersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessagesRepository messagesRepository;
    private final ChannelsRepository channelsRepository;
    private final UsersRepository usersRepository;

    public MessageService(MessagesRepository messagesRepository, 
                          ChannelsRepository channelsRepository, 
                          UsersRepository usersRepository) {
        this.messagesRepository = messagesRepository;
        this.channelsRepository = channelsRepository;
        this.usersRepository = usersRepository;
    }

    // 1. Fetch messages and map to safe DTOs
    public List<MessageResponseDTO> getMessagesByChannel(Integer channelId) {
        if (!channelsRepository.existsById(channelId)) {
            throw new ResourceNotFoundException("Channel not found", "No channel exists with ID: " + channelId);
        }
        
        List<Messages> messages = messagesRepository.findByChannel_ChannelIdOrderBySentAtAsc(channelId);
        
        return messages.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 2. Post message and return safe DTO
    // 2. Post message and return safe DTO
    @Transactional
    public MessageResponseDTO postMessage(Integer channelId, String userEmail, String content) {
        Channels channel = channelsRepository.findById(channelId)
                .orElseThrow(() -> new ResourceNotFoundException("Channel not found", "No channel exists with ID: " + channelId));
                
        Users sender = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "No user exists with email: " + userEmail));

        String roleName = sender.getRole().getRoleName();
        String channelType = channel.getChannelType();

        // --- SERVER-SIDE SECURITY GUARD (THE BOUNCER) ---
        
       // --- SERVER-SIDE SECURITY GUARD (THE BOUNCER) ---
        String normalizedRole = roleName != null ? roleName.toUpperCase().replace("ROLE_", "") : "";
        boolean isAdmin = normalizedRole.equals("CLUBADMIN") || normalizedRole.equals("ADMIN");

        // Rule 1: Only ADMIN can post to GLOBAL channels
        if ("GLOBAL".equals(channelType) && !isAdmin) {
            throw new SecurityException("Access Denied: Only Admins can post global announcements.");
        }

        // Rule 2: Regular members cannot post in LEADERSHIP lounge
        boolean isLeadOrCoLead = normalizedRole.contains("LEAD") || normalizedRole.contains("CO");
        if ("LEADERSHIP".equals(channelType) && !isAdmin && !isLeadOrCoLead) {
            throw new SecurityException("Access Denied: Members cannot access the Leadership Lounge.");
        }

        // Rule 3: Team channels are restricted to members of that specific team (unless Admin)
        if ("TEAM".equals(channelType) && !isAdmin) {
            if (sender.getTeam() == null || !sender.getTeam().getTeamId().equals(channel.getTeam().getTeamId())) {
                throw new SecurityException("Access Denied: You do not belong to this team's chat.");
            }
        }
        // ------------------------------------------------
        // ------------------------------------------------

        // If they pass the bouncer, save the message!
        Messages message = new Messages();
        message.setChannel(channel);
        message.setSender(sender);
        message.setContent(content);

        return mapToResponseDTO(messagesRepository.save(message));
    }

    // 3. Fetch all channels for sidebar navigation
    public List<ChannelResponseDTO> getAllChannels() {
        return channelsRepository.findAll().stream()
                .map(channel -> {
                    ChannelResponseDTO dto = new ChannelResponseDTO();
                    dto.setChannelId(channel.getChannelId());
                    dto.setChannelName(channel.getChannelName());
                    dto.setChannelType(channel.getChannelType());
                    dto.setTeamId(channel.getTeam() != null ? channel.getTeam().getTeamId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Helper mapping method
    private MessageResponseDTO mapToResponseDTO(Messages message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setMessageId(message.getMessageId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setSenderId(message.getSender().getUserId());
        dto.setSenderName(message.getSender().getUserName());
        dto.setChannelId(message.getChannel().getChannelId());
        return dto;
    }
}