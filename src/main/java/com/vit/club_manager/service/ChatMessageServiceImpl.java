package com.vit.club_manager.service;

import com.vit.club_manager.dto.ChatMessageDto;
import com.vit.club_manager.model.ChatMessageDocument;
import com.vit.club_manager.repository.ChatMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public ChatMessageDto processAndBroadcast(ChatMessageDto incomingDto, String authenticatedUser) {
        incomingDto.setSenderUsername(authenticatedUser);
        incomingDto.setTimestamp(Instant.now());

        ChatMessageDocument doc = new ChatMessageDocument(
            incomingDto.getChannelId(),
            incomingDto.getSenderUsername(),
            incomingDto.getSenderRole(),
            incomingDto.getContent()
        );
        chatMessageRepository.save(doc);    

        messagingTemplate.convertAndSend("/topic/channels/" + incomingDto.getChannelId(), incomingDto);
        return incomingDto;
    }

    @Override
    public List<ChatMessageDocument> getChannelHistory(Long channelId) {
        return chatMessageRepository.findTop50ByChannelIdOrderByTimestampDesc(channelId);
    }
    @Override
    public List<ChatMessageDto> getChannelHistory(Long channelId, Instant beforeTimestamp) {
        List<ChatMessageDocument> documents;
        
        // Check if we are loading initial chat, or scrolling up for older chat
        if (beforeTimestamp == null) {
            documents = chatMessageRepository.findTop50ByChannelIdOrderByTimestampDesc(channelId);
        } else {
            documents = chatMessageRepository.findTop50ByChannelIdAndTimestampLessThanOrderByTimestampDesc(channelId, beforeTimestamp);
        }
        
        // Map to DTOs just like before
        return documents.stream().map(doc -> {
            ChatMessageDto dto = new ChatMessageDto();
            dto.setChannelId(doc.getChannelId());
            dto.setSenderUsername(doc.getSenderUsername());
            dto.setSenderRole(doc.getSenderRole());
            dto.setContent(doc.getContent());
            dto.setTimestamp(doc.getTimestamp());
            return dto;
        }).toList();
    }
}