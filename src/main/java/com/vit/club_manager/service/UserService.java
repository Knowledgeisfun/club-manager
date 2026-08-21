package com.vit.club_manager.service;

import com.vit.club_manager.config.AppConstants;
import com.vit.club_manager.dto.UserRegistrationDTO;
import com.vit.club_manager.dto.UserResponseDTO;
import com.vit.club_manager.dto.UserUpdateRequest;
import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.exception.ResourceNotFoundException;
import com.vit.club_manager.exception.UserAlreadyExistsException;
import com.vit.club_manager.repository.RolesRepository;
import com.vit.club_manager.repository.UsersRepository;
import com.vit.club_manager.repository.TeamsRepository;

import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service 
public class UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamsRepository teamRepository; 

    // Constructor Injection
    public UserService(UsersRepository usersRepository, 
                       RolesRepository rolesRepository, 
                       PasswordEncoder passwordEncoder,
                       TeamsRepository teamRepository) { 
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.teamRepository = teamRepository; 
    }

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO dto) {
        // 1. Data Sanitization
        String safeEmail = dto.getEmail().trim().toLowerCase();
        String safeUsername = dto.getUsername().trim();
        String safeRegNumber = dto.getRegistrationNumber().trim(); 

        // 2. Business Rule: Check Uniqueness (Fail Fast)
        if (usersRepository.existsByEmail(safeEmail)) {
            throw new UserAlreadyExistsException(
                "A user with this email is already registered.",
                "Please use a different email address to register." 
            );
        }

        if (usersRepository.existsByRegistrationNumber(safeRegNumber)) {
            throw new UserAlreadyExistsException(
                "A user with the registration number " + safeRegNumber + " is already registered.",
                "Please verify your registration number or contact the club administrator." 
            );
        }

        // 3. Create the Entity
        Users newUser = new Users();
        newUser.setUserName(safeUsername);
        newUser.setEmail(safeEmail);
        newUser.setRegistrationNumber(safeRegNumber);
        
        // 4. Hash the password 
        newUser.setPasswordHash(passwordEncoder.encode(dto.getPassword())); 

        // 5. Business Rule: Force Default Role
        Roles defaultRole = rolesRepository.findByRoleName(AppConstants.ROLE_MEMBER);
        newUser.setRole(defaultRole);   

        // 6. Save to Database
        Users savedUser = usersRepository.save(newUser);

        // 7. Convert to DTO and return
        return mapToResponseDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO assignUserToTeam(Integer userId, Integer teamId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found", 
                        "No user exists with the ID: " + userId));

        Teams team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Team not found", 
                        "No team exists with the ID: " + teamId));

        user.setTeam(team); 
        Users updatedUser = usersRepository.save(user);
        
        return mapToResponseDTO(updatedUser); 
    }

    // ==========================================
    // ADMIN CONTROLS: EDIT & REMOVE
    // ==========================================

    @Transactional
    public void deleteUser(Integer id) {
        if (!usersRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "User not found", 
                "Cannot delete. No user exists with the ID: " + id
            );
        }
        usersRepository.deleteById(id);
    }

    @Transactional
    public UserResponseDTO updateUser(Integer id, UserUpdateRequest request) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found", 
                    "Cannot update. No user exists with the ID: " + id
                ));
        
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setUserName(request.getFullName());
        }
        if (request.getRegistrationNumber() != null && !request.getRegistrationNumber().trim().isEmpty()) {
            user.setRegistrationNumber(request.getRegistrationNumber());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            Roles newRole = rolesRepository.findByRoleName(request.getRole());
            if (newRole != null) {
                user.setRole(newRole);
            }
        }

        if (request.getTeamId() != null) {
            Teams newTeam = teamRepository.findById(request.getTeamId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Team not found", 
                        "Cannot assign user to a non-existent team ID: " + request.getTeamId()
                    ));
            user.setTeam(newTeam);
        }

        Users updatedUser = usersRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    // ==========================================
    // PASSWORD MANAGEMENT
    // ==========================================

    @Transactional
    public void updatePasswordAndClearFlag(String email, String newEncodedPassword) {
        // 1. Find the user by email
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "No user with email: " + email));
        
        // 2. Apply the new hashed password (FIXED: mapped to setPasswordHash)
        user.setPasswordHash(newEncodedPassword);
        
        // 3. MOST IMPORTANT: Flip the flag so they aren't forced to change it again!
        user.setRequiresPasswordChange(false);
        
        // 4. Save to the database
        usersRepository.save(user);
    }
    
    // ==========================================
    // DATA RETRIEVAL
    // ==========================================

    public List<UserResponseDTO> getAllUsers() {
        List<Users> allUsers = usersRepository.findAll();
        return allUsers.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert an Entity into our safe Output DTO
    private UserResponseDTO mapToResponseDTO(Users user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getUserId());
        responseDTO.setUsername(user.getUserName());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRegistrationNumber(user.getRegistrationNumber());
        
        if (user.getRole() != null) {
            responseDTO.setRole(user.getRole().getRoleName());
        }
        
        if (user.getTeam() != null) {
            responseDTO.setTeamId(user.getTeam().getTeamId());
        }
        
        return responseDTO;
    }
}