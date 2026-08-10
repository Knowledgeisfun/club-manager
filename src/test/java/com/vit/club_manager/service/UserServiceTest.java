package com.vit.club_manager.service;

import com.vit.club_manager.dto.UserRegistrationDTO;
import com.vit.club_manager.dto.UserResponseDTO;
import com.vit.club_manager.exception.UserAlreadyExistsException;
import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.repository.RolesRepository;
import com.vit.club_manager.repository.TeamsRepository;
import com.vit.club_manager.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vit.club_manager.config.AppConstants;
import com.vit.club_manager.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

// This tells JUnit 5 to enable Mockito's fake object creation
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // @Mock creates completely fake, empty versions of our dependencies.
    // They will not connect to MySQL. They only do exactly what we tell them to do.
    @Mock
    private UsersRepository usersRepository;
    
    @Mock
    private RolesRepository rolesRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private TeamsRepository teamRepository;

    // @InjectMocks creates a REAL UserService, but forces the fake @Mocks inside of it!
    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_WhenEmailAlreadyExists_ShouldThrowException() {
        
        // 1. ARRANGE (Set up our data)
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("sanjay@vitbhopal.ac.in"); // We sanitize to lowercase in the service, so use lowercase here
        dto.setUsername("Sanjay");
        dto.setRegistrationNumber("23BCE11016");
        dto.setPassword("secure123");

        // Here is the Mockito magic:
        // We tell our fake database: "If the service asks if this email exists, immediately say TRUE."
        when(usersRepository.existsByEmail("sanjay@vitbhopal.ac.in")).thenReturn(true);

        // 2 & 3. ACT & ASSERT
        // We assert that calling registerUser(dto) MUST throw a UserAlreadyExistsException
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class, 
                () -> userService.registerUser(dto)
        );

        // 4. VERIFY
        // Let's make sure the exception has the exact message we expect
        assertEquals("A user with this email is already registered.", exception.getMessage());
        
        // Let's prove that the code stopped running and NEVER tried to save to the database!
        verify(usersRepository, never()).save(any());
    }

    @Test
    void registerUser_WhenValidData_ShouldSaveAndReturnResponse() {
        // 1. ARRANGE (Set up our input DTO)
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("sanjay@vitbhopal.ac.in");
        dto.setUsername("Sanjay Vinod");
        dto.setRegistrationNumber("23BCE11016");
        dto.setPassword("secure123");

        // Mock the default Role that the database would return
        Roles defaultRole = new Roles();
        defaultRole.setRoleId(1);
        defaultRole.setRoleName(AppConstants.ROLE_MEMBER);

        // Mock the final User entity that Hibernate would return after saving
        Users savedUser = new Users();
        savedUser.setUserId(1);
        savedUser.setUserName("Sanjay Vinod");
        savedUser.setEmail("sanjay@vitbhopal.ac.in");
        savedUser.setRegistrationNumber("23BCE11016");
        savedUser.setRole(defaultRole);
        // (We don't need to set the password on the mock returned user because our DTO doesn't expose it anyway!)

        // Teach the mocks how to behave to allow a successful registration:
        when(usersRepository.existsByEmail("sanjay@vitbhopal.ac.in")).thenReturn(false); // Email is free
        when(usersRepository.existsByRegistrationNumber("23BCE11016")).thenReturn(false); // Reg Num is free
        
        // Assuming your rolesRepository returns an Optional<Roles>
       when(rolesRepository.findByRoleName(AppConstants.ROLE_MEMBER)).thenReturn(defaultRole);
        
        // Fake the password hashing
        when(passwordEncoder.encode("secure123")).thenReturn("hashed_fake_password_string");
        
        // When the service tries to save ANY User object, return our mocked savedUser
        when(usersRepository.save(any(Users.class))).thenReturn(savedUser);

        // 2. ACT
        UserResponseDTO response = userService.registerUser(dto);

        // 3. ASSERT (Verify the output matches our expectations)
        assertNotNull(response);
        assertEquals("Sanjay Vinod", response.getUsername());
        assertEquals("sanjay@vitbhopal.ac.in", response.getEmail());
        assertEquals(AppConstants.ROLE_MEMBER, response.getRole());

        // 4. VERIFY (Ensure the service actually called the right methods behind the scenes)
        verify(passwordEncoder, times(1)).encode("secure123"); // Prove we hashed the password
        verify(usersRepository, times(1)).save(any(Users.class)); // Prove we called save() exactly once
    }

    @Test
    void assignUserToTeam_WhenValidIds_ShouldAssignAndReturn() {
        // 1. ARRANGE
        // Create a fake User who currently has no team
        Users mockUser = new Users();
        mockUser.setUserId(1);
        mockUser.setUserName("Sanjay Vinod K"); // Using your confirmed exact name!
        mockUser.setEmail("sanjay@vitbhopal.ac.in");

        // Create a fake Team
        Teams mockTeam = new Teams();
        mockTeam.setTeamId(2);
        mockTeam.setTeamName("Development Team");

        // Teach the mocks: When asked for User 1, return the mockUser. 
        // When asked for Team 2, return the mockTeam.
        when(usersRepository.findById(1)).thenReturn(java.util.Optional.of(mockUser));
        when(teamRepository.findById(2)).thenReturn(java.util.Optional.of(mockTeam));
        
        // When the service saves the user, return the user
        when(usersRepository.save(any(Users.class))).thenReturn(mockUser);

        // 2. ACT
        // Call the method to assign User 1 to Team 2
        UserResponseDTO response = userService.assignUserToTeam(1, 2);

        // 3. ASSERT
        assertNotNull(response);
        assertEquals("Sanjay Vinod K", response.getUsername());
        
        // 4. VERIFY
        // Prove that the user's team was actually updated before saving!
        assertEquals(mockTeam, mockUser.getTeam()); 
        
        // Prove that the repository's save method was called exactly once
        verify(usersRepository, times(1)).save(mockUser);
    }

    @Test
    void assignUserToTeam_WhenUserNotFound_ShouldThrowException() {
        // 1. ARRANGE
        Integer invalidUserId = 999;
        Integer teamId = 2;

        // Tell the mock database: "When asked for User 999, return an empty Optional."
        when(usersRepository.findById(invalidUserId)).thenReturn(java.util.Optional.empty());

        // 2 & 3. ACT & ASSERT
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.assignUserToTeam(invalidUserId, teamId)
        );

        // 4. VERIFY
        // Check that the exception has our exact expected message
        assertEquals("User not found", exception.getMessage());
        
        // PROVE IT FAILED FAST:
        // The service should never have even tried to look up the team
        verify(teamRepository, never()).findById(anyInt());
        
        // The service should definitely never try to save!
        verify(usersRepository, never()).save(any());
    }
}