package com.vit.club_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.repository.TeamsRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass Spring Security for now
class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Notice we mock the Repository here, because the Controller injects it directly!
    @MockBean
    private TeamsRepository teamsRepository;

    @Test
    @SuppressWarnings("null")
    void getAllTeams_ShouldReturn200OkAndListOfTeams() throws Exception {
        // 1. ARRANGE
        Teams team1 = new Teams();
        team1.setTeamId(1);
        team1.setTeamName("marketing");

        Teams team2 = new Teams();
        team2.setTeamId(2);
        team2.setTeamName("photography");

        List<Teams> mockTeams = Arrays.asList(team1, team2);

        // Tell the fake database to return our list
        Mockito.when(teamsRepository.findAll()).thenReturn(mockTeams);

        // 2 & 3. ACT & ASSERT
        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].teamName").value("marketing"))
                .andExpect(jsonPath("$[1].teamName").value("photography"));
    }

    @Test
    @SuppressWarnings("null")
    void createTeam_ShouldReturn201CreatedAndSavedTeam() throws Exception {
        // 1. ARRANGE
        // This is what the client sends (no ID yet)
        Teams requestTeam = new Teams();
        requestTeam.setTeamName("eventmanagement");

        // This is what the database returns (ID assigned)
        Teams savedTeam = new Teams();
        savedTeam.setTeamId(3);
        savedTeam.setTeamName("eventmanagement");

        // Tell the mock repository what to return when it saves
        Mockito.when(teamsRepository.save(Mockito.any(Teams.class))).thenReturn(savedTeam);

        // 2 & 3. ACT & ASSERT
        mockMvc.perform(post("/api/teams")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(requestTeam))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.teamId").value(3)) // <-- Changed "id" to "teamId" // Proves it returned the saved database version
                .andExpect(jsonPath("$.teamName").value("eventmanagement"));
    }
}