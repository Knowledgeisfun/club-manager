package com.vit.club_manager.config;

import com.vit.club_manager.security.WebSocketJwtInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Pulls your Vercel URL dynamically from application.properties
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    private final WebSocketJwtInterceptor jwtInterceptor;

    // Inject the interceptor you just built    
    public WebSocketConfig(WebSocketJwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

   @Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Fall back to localhost:5173 if the property is missing or uninitialized
    String[] origins = (allowedOrigins != null && allowedOrigins.length > 0) 
        ? allowedOrigins 
        : new String[]{"http://localhost:5173", "http://127.0.0.1:5173"};

    registry.addEndpoint("/ws-chat")
            .setAllowedOrigins(origins)
            .withSockJS(); 
}

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Topics that Spring Boot broadcasts OUT to React
        registry.enableSimpleBroker("/topic");
        
        // Prefix for messages coming IN from React
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Plug the security firewall into the inbound network pipe
        registration.interceptors(jwtInterceptor);
    }
}