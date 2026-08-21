    package com.vit.club_manager.controller;

    import com.vit.club_manager.dto.UserRegistrationDTO;
    import com.vit.club_manager.dto.UserResponseDTO;
    import com.vit.club_manager.dto.UserUpdateRequest;
    import com.vit.club_manager.service.UserService;

    import jakarta.validation.Valid;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;
    import java.util.List;

    @RestController 
    @RequestMapping("/api/users") 
    @CrossOrigin(origins = "${app.cors.allowed-origins}")
    public class UserController {

        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }

        @PostMapping("/register")
        // FIXED: Added the underscore to match your database!
        @PreAuthorize("hasAnyAuthority('ROLE_CLUB_ADMIN', 'ROLE_CLUBADMIN', 'ROLE_ADMIN')") 
        public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
            UserResponseDTO responseDTO = userService.registerUser(registrationDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        }

        @GetMapping
        @PreAuthorize("isAuthenticated()") 
        public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
            List<UserResponseDTO> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        }

        @PutMapping("/{userId}/team/{teamId}")
        @PreAuthorize("hasAnyAuthority('ROLE_CLUB_ADMIN', 'ROLE_CLUBADMIN', 'ROLE_ADMIN')") 
        public ResponseEntity<UserResponseDTO> assignTeamToUser(
                @PathVariable Integer userId, 
                @PathVariable Integer teamId) {
            UserResponseDTO updatedUser = userService.assignUserToTeam(userId, teamId);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyAuthority('ROLE_CLUB_ADMIN', 'ROLE_CLUBADMIN', 'ROLE_ADMIN')")
        public ResponseEntity<?> deleteMember(@PathVariable Integer id) {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("{\"message\": \"Member successfully removed.\"}");
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyAuthority('ROLE_CLUB_ADMIN', 'ROLE_CLUBADMIN', 'ROLE_ADMIN')")
        public ResponseEntity<UserResponseDTO> updateMember(@PathVariable Integer id, @RequestBody UserUpdateRequest request) {
            UserResponseDTO updatedUser = userService.updateUser(id, request);
            return ResponseEntity.ok(updatedUser);
        }
    }