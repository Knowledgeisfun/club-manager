package com.vit.club_manager.controller;

import com.vit.club_manager.dto.UserRegistrationDTO;
import com.vit.club_manager.dto.UserResponseDTO;
import com.vit.club_manager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController // Tells Spring this class handles web requests and returns JSON
@RequestMapping("/api/users") // The base URL for all endpoints in this class
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @Valid - check @Not Blank and @email in dto user reg
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        
        // 1. Hand the DTO to the Service Layer to do the heavy lifting
        UserResponseDTO responseDTO = userService.registerUser(registrationDTO);
        
        // 2. Return the safe response with a 201 CREATED status code
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Assign a user to a team
    @PutMapping("/{userId}/team/{teamId}")
    public ResponseEntity<UserResponseDTO> assignTeamToUser(
            @PathVariable Integer userId, 
            @PathVariable Integer teamId) {
        
        UserResponseDTO updatedUser = userService.assignUserToTeam(userId, teamId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}