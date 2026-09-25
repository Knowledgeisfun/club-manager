package com.vit.club_manager.service;

import com.vit.club_manager.dto.ChatMessageDto;
import com.vit.club_manager.model.ChatMessageDocument;

import java.time.Instant;
import java.util.List;

public interface ChatMessageService {
    ChatMessageDto processAndBroadcast(ChatMessageDto incomingDto, String authenticatedUser);
    List<ChatMessageDocument> getChannelHistory(Long channelId);
    // Update the method signature to accept the timestamp
    List<ChatMessageDto> getChannelHistory(Long channelId, Instant beforeTimestamp);
    
}