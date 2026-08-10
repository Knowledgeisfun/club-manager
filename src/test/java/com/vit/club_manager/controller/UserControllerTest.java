package com.vit.club_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vit.club_manager.dto.UserRegistrationDTO;
import com.vit.club_manager.dto.UserResponseDTO;
import com.vit.club_manager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.util.Objects;
import java.util.List;
import java.util.Arrays;

// @WebMvcTest tells Spring to ONLY load the web layer (Controllers), not the database!
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Turns off Spring Security just for this test so we don't get 401 Unauthorized
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // Our fake Postman!

    @Autowired
    private ObjectMapper objectMapper; // Converts Java objects into JSON strings

    // Since we are testing the Controller, we must MOCK the Service layer
    @MockBean
    private UserService userService;

    @Test
    @SuppressWarnings("null")
    void getAllUsers_ShouldReturn200OkAndListOfUsers() throws Exception {
        // 1. ARRANGE
        // Create a couple of fake user responses
        UserResponseDTO user1 = new UserResponseDTO();
        user1.setId(1);
        user1.setUsername("Sanjay Vinod K");

        UserResponseDTO user2 = new UserResponseDTO();
        user2.setId(2);
        user2.setUsername("Test User");

        List<UserResponseDTO> mockUsers = Arrays.asList(user1, user2);

        // Tell the mock service to return our list
        Mockito.when(userService.getAllUsers()).thenReturn(mockUsers);

        // 2 & 3. ACT & ASSERT
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()) // Expect 200 OK
                // $ means the root of the JSON. Since it's a List, it returns a JSON Array.
                // We check that the array size is exactly 2!
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].username").value("Sanjay Vinod K"))
                .andExpect(jsonPath("$[1].username").value("Test User"));
    }

    @Test
    void registerUser_WhenValidPayload_ShouldReturn201Created() throws Exception {
        // 1. ARRANGE
        // Create the incoming JSON payload (The Request)
        UserRegistrationDTO requestDto = new UserRegistrationDTO();
        requestDto.setUsername("Sanjay Vinod K");
        requestDto.setEmail("sanjay@vitbhopal.ac.in");
        requestDto.setRegistrationNumber("23BCE11016");
        requestDto.setPassword("secure123");

        // Create the expected output (The Response)
        UserResponseDTO responseDto = new UserResponseDTO();
        responseDto.setId(1);
        responseDto.setUsername("Sanjay Vinod K");
        responseDto.setEmail("sanjay@vitbhopal.ac.in");
        responseDto.setRole("member");

        // Tell our mocked service what to return when the controller calls it
        Mockito.when(userService.registerUser(Mockito.any(UserRegistrationDTO.class)))
               .thenReturn(responseDto);

        // 2 & 3. ACT & ASSERT
        // We use MockMvc to perform a fake POST request!
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE) // "Hey Controller, I'm sending JSON"
                // Wrap the objectMapper call inside Objects.requireNonNull()
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestDto))))// Convert DTO to JSON
                
                // Assertions happen right here in the chain!
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.username").value("Sanjay Vinod K")) // Check the JSON body
                .andExpect(jsonPath("$.email").value("sanjay@vitbhopal.ac.in"))
                .andExpect(jsonPath("$.role").value("member"));
    }

    @Test
    @SuppressWarnings("null")
    void registerUser_WhenInvalidPayload_ShouldReturn400BadRequest() throws Exception {
        // 1. ARRANGE
        // Create a DTO that VIOLATES our validation rules
        UserRegistrationDTO badRequestDto = new UserRegistrationDTO();
        badRequestDto.setUsername(""); // Blank name!
        badRequestDto.setEmail("not-an-email"); // Invalid email format!
        badRequestDto.setRegistrationNumber("23BCE11016");
        badRequestDto.setPassword("123");

        // 2 & 3. ACT & ASSERT
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(badRequestDto)))
                
                // Assert that the Controller intercepts the bad data and throws a 400 Bad Request
                .andExpect(status().isBadRequest());
                
        // Notice we didn't even have to mock the UserService here! 
        // Spring blocks the request before it even reaches the Service.
    }

    @Test
    @SuppressWarnings("null")
    void assignTeamToUser_WhenValidIds_ShouldReturn200Ok() throws Exception {
        // 1. ARRANGE
        Integer userId = 1;
        Integer teamId = 1;

        UserResponseDTO responseDto = new UserResponseDTO();
        responseDto.setId(userId);
        responseDto.setUsername("Sanjay Vinod K");
        responseDto.setRole("member");

        // Tell the mock service what to return
        Mockito.when(userService.assignUserToTeam(userId, teamId)).thenReturn(responseDto);

        // 2 & 3. ACT & ASSERT
        // Notice how we pass the IDs directly into the URL string using MockMvc
        mockMvc.perform(put("/api/users/{userId}/team/{teamId}", userId, teamId))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.username").value("Sanjay Vinod K"));
    }
}