package com.vit.club_manager.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class WebSocketJwtInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // Inject your custom utilities
    public WebSocketJwtInterceptor(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Only intercept the initial connection attempt
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            
            // 1. Get the list of Authorization headers
            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            // 2. Check if it exists
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String bearerToken = authHeaders.get(0);

                // 3. Check for prefix and strip it
                if (bearerToken.startsWith("Bearer ")) {
                    String actualToken = bearerToken.substring(7);

                    // 4. Validate using your exact JwtUtil method
                    if (jwtUtil.validateToken(actualToken)) {
                        
                        // 5. Extract email
                        String username = jwtUtil.extractUsername(actualToken);
                        
                        if (username != null) {
                            // 6. Load user using your custom service
                            UserDetails userDetails = userDetailsService.loadUserByUsername(username); 

                            // 7. Apply the authentication to this WebSocket session
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            accessor.setUser(auth);
                        }
                    }
                }
            }
        }

        return message; 
    }
}