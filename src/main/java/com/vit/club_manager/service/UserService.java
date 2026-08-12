package com.vit.club_manager.service;

import com.vit.club_manager.config.AppConstants;
import com.vit.club_manager.dto.UserRegistrationDTO;
import com.vit.club_manager.dto.UserResponseDTO;
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



@Service // Tells Spring IoC Container to manage this class
public class UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final TeamsRepository teamRepository; 

    // Constructor Injection
    public UserService(UsersRepository usersRepository, 
                       RolesRepository rolesRepository, 
                       org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
                       TeamsRepository teamRepository) { 
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.teamRepository = teamRepository; 
    }

    //  It guarantees that a group of database operations is "All or Nothing.

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO dto) {

        // 1. Data Sanitization
        String safeEmail = dto.getEmail().trim().toLowerCase();
        String safeUsername = dto.getUsername().trim();
        String safeRegNumber = dto.getRegistrationNumber().trim(); // Sanitize reg number too

        // 2. Business Rule: Check Uniqueness (Fail Fast)
        if (usersRepository.existsByEmail(safeEmail)) {
            throw new UserAlreadyExistsException(
                "A user with this email is already registered.",
                "Please use a different email address to register." // Pass the details here!
            );
        }

        if (usersRepository.existsByRegistrationNumber(safeRegNumber)) {
            throw new UserAlreadyExistsException(
                "A user with the registration number " + safeRegNumber + " is already registered.",
                "Please verify your registration number or contact the club administrator." // Pass the details here!
            );
        }

        // 3. Create the Entity
        Users newUser = new Users();
        newUser.setUserName(safeUsername);
        newUser.setEmail(safeEmail);
        newUser.setRegistrationNumber(safeRegNumber);
        
        // 4. Hash the password (For now, we store plain text until we add Spring Security)
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
        
        // 1. Find the user (or throw 404)
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found", 
                        "No user exists with the ID: " + userId));

        // 2. Find the team (or throw 404)
        Teams team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Team not found", 
                        "No team exists with the ID: " + teamId));

        // 3. Assign the team to the user
        user.setTeam(team); 

        // 4. Save and return the DTO
        Users updatedUser = usersRepository.save(user);
        
        // Assuming you have a method to convert the entity to a DTO
        // If not, just return a success string or the raw entity for now.
        return mapToResponseDTO(updatedUser); 
    }

    public void changePassword(String email, String rawNewPassword) {
        Users user = usersRepository.findByEmail(email)
               .orElseThrow(() -> new ResourceNotFoundException("User not found", "No user exists with the provided email."));
        // Hash the new password
        user.setPasswordHash(passwordEncoder.encode(rawNewPassword));
        
        // Flip the flag so they never get trapped on this screen again!
        user.setRequiresPasswordChange(false);
        
        usersRepository.save(user);
    }
    
    

    public List<UserResponseDTO> getAllUsers() {
        // 1. Fetch all users from the database
        List<Users> allUsers = usersRepository.findAll();
        
        // 2. Convert every single 'Users' entity into a safe 'UserResponseDTO'
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