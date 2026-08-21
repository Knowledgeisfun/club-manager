package com.vit.club_manager.controller;

import com.vit.club_manager.security.CustomUserDetails;
import com.vit.club_manager.dto.AuthRequestDTO;
import com.vit.club_manager.dto.AuthResponseDTO;
import com.vit.club_manager.security.JwtUtil;
// Import your UserService and PasswordEncoder
import com.vit.club_manager.service.UserService; 
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    // Inject the Service and Encoder to handle the password update
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    // ==========================================
    // NEW ENDPOINT: Handle Password Change
    // ==========================================
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, Authentication authentication) {
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters.");
        }

        // 1. Get the currently logged-in user's email from the JWT context
        String email = authentication.getName();

        // 2. Hash the new password
        String encodedPassword = passwordEncoder.encode(newPassword);

        // 3. Delegate to your UserService to save it to the DB
        try {
            userService.updatePasswordAndClearFlag(email, encodedPassword);
            return ResponseEntity.ok().body("{\"message\": \"Password updated successfully.\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Could not update password.\"}");
        }
    }
}