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
//controler + rest = restcontroller sepcify for rest api
@RestController
//this is the entry pont of this auth controller
@RequestMapping("/api/auth")
public class AuthController {
    //depedency injection tru constructore of the given classes we need to use 
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

    /**
     * Primary authentication endpoint for user login.
     * 
     * Workflow:
     * 1. Input Mapping: Intercepts the incoming JSON payload and maps it to AuthRequestDTO via @RequestBody.
     * 2. Authentication: Passes the extracted email and password to Spring's AuthenticationManager for database validation.
     * 3. Token Generation: Upon successful authentication, retrieves CustomUserDetails and mints a stateless JWT for the session.
     * 4. State Check: Checks if the user is flagged for a mandatory password reset (e.g., newly provisioned members).
     * 5. Structured Response: Wraps the JWT and password flag in a custom AuthResponseDTO, providing the React client with a predictable JSON structure for routing.
     */
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

   
        /**
     * Endpoint for mandatory password resets (e.g., initial login for admin-provisioned accounts).
     * 
     * Workflow:
     * 1. Payload Extraction: Receives the new password via a generic JSON Map.
     * 2. Validation: Enforces a minimum length requirement of 8 characters, returning a 400 Bad Request if invalid.
     * 3. Identity Resolution: Extracts the authenticated user's email directly from the stateless JWT context (no email required from frontend).
     * 4. Cryptography: Securely hashes the raw password using the injected PasswordEncoder.
     * 5. Persistence: Delegates to UserService to update the database record and clear the 'requiresPasswordChange' flag.
     * 6. Response: Returns a generic ResponseEntity <?> containing a simple JSON success or error message.
     */
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