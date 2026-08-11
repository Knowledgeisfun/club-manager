package com.vit.club_manager.config;

import com.vit.club_manager.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    //This simply tells Spring: "Please take your internal AuthenticationManager and make it available for my AuthController to use." its hidden default
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (Cross-Site Request Forgery) protection. 
            // This is required so Postman/Android can send POST requests without a special token.
            .csrf(csrf -> csrf.disable())
            
            // 2. Configure endpoint rules
            .authorizeHttpRequests(auth -> auth
                // Make both registration AND fetching users public
                //.requestMatchers("/api/users/register").permitAll()
               // .requestMatchers("/api/users").permitAll() 
                //.requestMatchers("/api/teams/**").permitAll()
                //.requestMatchers("/api/users/*/team/*").permitAll()
                .requestMatchers("/api/auth/login", "/api/users/register").permitAll()
                
                .anyRequest().authenticated()
            )

            // 2. VERY IMPORTANT: Tell Spring to stop using stateful sessions
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Put our Bouncer at the front door, right before Spring's default login filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
            return http.build();
        
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}