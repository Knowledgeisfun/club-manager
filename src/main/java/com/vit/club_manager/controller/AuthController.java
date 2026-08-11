package com.vit.club_manager.controller;

import com.vit.club_manager.dto.AuthRequestDTO;
import com.vit.club_manager.dto.AuthResponseDTO;
import com.vit.club_manager.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Spring's built-in tool that triggers your CustomUserDetailsService
    private final AuthenticationManager authenticationManager;
    
    // Your custom token generator
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        
        // 1. Tell Spring to verify the email and password against the database
        // If the password is wrong, this line will immediately throw an Exception and stop.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(), 
                        authRequest.getPassword()
                )
        );

        // 2. If it succeeds, grab the UserDetails object that your CustomUserDetailsService created
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. Generate the JWT using their email
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        // 4. Return the token to the frontend
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }
}