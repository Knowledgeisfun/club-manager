package com.vit.club_manager.controller;

import com.vit.club_manager.dto.ChatMessageDto;
import com.vit.club_manager.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

// Use @Controller instead of @RestController for WebSocket mappings
@Controller
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;

    public ChatWebSocketController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // Catches messages sent from React to "/app/chat.sendMessage"
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto incomingDto, Principal principal) {
        
        // Extract identity from the JWT Token verified during the STOMP handshake
        String authenticatedUser = principal.getName();
        
        // The MongoDB Service handles saving the document and broadcasting it down the pipe
        chatMessageService.processAndBroadcast(incomingDto, authenticatedUser);
    }
}