package com.vit.club_manager.controller;

import com.vit.club_manager.dto.ChannelResponseDTO;
import com.vit.club_manager.security.CustomUserDetails;
import com.vit.club_manager.service.ChannelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    /**
     * Controller managing access to chat channels and lounges.
     * 
     * Workflow:
     * 1. Security Gate: @PreAuthorize ensures only authenticated users with valid JWTs can hit this endpoint.
     * 2. Stateless Identity: Leverages @AuthenticationPrincipal to extract the user's identity directly from the SecurityContext (no JSON payload required).
     * 3. Role-Based Access Control (RBAC): Delegates to ChannelService to query the database and filter available channels based on the user's specific role (e.g., Member vs. Admin).
     * 4. Response: Returns a tailored List of ChannelResponseDTOs containing only the groups this specific user is authorized to view.
     */
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // Anyone logged in can hit this endpoint
    public ResponseEntity<List<ChannelResponseDTO>> getMyChannels(
            // Spring magically injects the currently logged-in user here!
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        // Pass the user to the service to filter the channels
        List<ChannelResponseDTO> myChannels = channelService.getChannelsForCurrentUser(currentUser);
        
        return ResponseEntity.ok(myChannels);
    }
}