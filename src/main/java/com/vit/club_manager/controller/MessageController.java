package com.vit.club_manager.controller;

import com.vit.club_manager.dto.MessageRequestDTO;
import com.vit.club_manager.dto.MessageResponseDTO;
import com.vit.club_manager.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/{channelId}/messages")
    // FIX: Let all logged-in users fetch messages (Frontend hides rooms they shouldn't click)
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<List<MessageResponseDTO>> getChannelMessages(@PathVariable Integer channelId) {
        List<MessageResponseDTO> messages = messageService.getMessagesByChannel(channelId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{channelId}/messages")
    // FIX: Let all logged-in users try to post. The Service will block them if they lack permissions!
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<MessageResponseDTO> postMessage(
            @PathVariable Integer channelId, 
            @RequestBody @Valid MessageRequestDTO requestDTO) {
        
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        MessageResponseDTO savedMessage = messageService.postMessage(
                channelId, 
                currentUserEmail, 
                requestDTO.getContent()
        );
        
        return ResponseEntity.status(201).body(savedMessage);
    }
}