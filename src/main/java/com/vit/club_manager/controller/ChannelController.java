package com.vit.club_manager.controller;

import com.vit.club_manager.dto.ChannelResponseDTO;
import com.vit.club_manager.model.Channels;
import com.vit.club_manager.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    // Constructor Injection of ChannelService
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CLUBADMIN') or hasAuthority('ROLE_TEAMLEAD') or hasAuthority('ROLE_COLEAD') or hasAuthority('ROLE_MEMBER') or hasAuthority('MEMBER')")
    public ResponseEntity<List<ChannelResponseDTO>> getAllChannels() {
        List<ChannelResponseDTO> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }
}