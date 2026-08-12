package com.vit.club_manager.controller;

import com.vit.club_manager.security.CustomUserDetails;
import com.vit.club_manager.dto.AuthRequestDTO;
import com.vit.club_manager.dto.AuthResponseDTO;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.repository.UsersRepository;
import com.vit.club_manager.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    // Notice: UsersRepository is completely gone!
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(), 
                        authRequest.getPassword()
                )
        );

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(customUserDetails);
        boolean requiresChange = customUserDetails.getUser().isRequiresPasswordChange();

        return ResponseEntity.ok(new AuthResponseDTO(jwt, requiresChange));
    }
}