package com.vit.club_manager.service;

import com.vit.club_manager.config.AppConstants;
import com.vit.club_manager.dto.UserRegistrationDTO;
import com.vit.club_manager.dto.UserResponseDTO;
import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.exception.UserAlreadyExistsException;
import com.vit.club_manager.repository.RolesRepository;
import com.vit.club_manager.repository.UsersRepository;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service // Tells Spring IoC Container to manage this class
public class UserService {

    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    // Constructor Injection
    public UserService(UsersRepository usersRepository, RolesRepository rolesRepository) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
    }

    //  It guarantees that a group of database operations is "All or Nothing.

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationDTO dto) {

        // 1. Data Sanitization

        String safeEmail = dto.getEmail().trim().toLowerCase();
        String safeUsername = dto.getUsername().trim();

        // 2. Business Rule: Check Uniqueness (Fail Fast)
        if (usersRepository.existsByEmail(safeEmail)) {
            throw new UserAlreadyExistsException("A user with this email is already registered.");
        }

        //3. Create the Entity
        Users newUser = new Users();
        newUser.setUserName(safeUsername);
        newUser.setEmail(safeEmail);
        newUser.setRegistrationNumber(dto.getRegistrationNumber());
        
        // 4. Hash the password (For now, we store plain text until we add Spring Security)
        newUser.setPasswordHash(dto.getPassword()); 

        // 5. Business Rule: Force Default Role
        Roles defaultRole = rolesRepository.findByRoleName(AppConstants.ROLE_MEMBER);
        newUser.setRole(defaultRole);

        // 6. Save to Database
        Users savedUser = usersRepository.save(newUser);

        // 7. Convert to DTO and return
        return mapToResponseDTO(savedUser);
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