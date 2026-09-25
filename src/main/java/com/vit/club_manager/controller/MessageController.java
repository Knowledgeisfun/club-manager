package com.vit.club_manager.controller;

import com.vit.club_manager.dto.ChatMessageDto;
import com.vit.club_manager.model.ChatMessageDocument;
import com.vit.club_manager.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class MessageController {

    // Inject the new MongoDB service instead of the old legacy service
    private final ChatMessageService chatMessageService;

    public MessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // Keep this endpoint: React uses it to load history when switching channels
    @GetMapping("/{channelId}/messages")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<List<ChatMessageDto>> getChannelMessages(
        @PathVariable Long channelId,
        // The new optional parameter React will send when scrolling up
        @RequestParam(required = false) Instant beforeTimestamp) {
    
    List<ChatMessageDto> messages = chatMessageService.getChannelHistory(channelId, beforeTimestamp);
    return ResponseEntity.ok(messages);
}
    
    // DELTED: @PostMapping("/{channelId}/messages")
    // STOMP WebSockets now handle all message publishing, not HTTP POST!
}