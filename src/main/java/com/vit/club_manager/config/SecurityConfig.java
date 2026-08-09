package com.vit.club_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (Cross-Site Request Forgery) protection. 
            // This is required so Postman/Android can send POST requests without a special token.
            .csrf(csrf -> csrf.disable())
            
            // 2. Configure endpoint rules
            .authorizeHttpRequests(auth -> auth
                // Make both registration AND fetching users public
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/users").permitAll() 
                
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}