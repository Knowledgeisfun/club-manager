package com.vit.club_manager.controller;

import com.vit.club_manager.model.Teams;
import com.vit.club_manager.repository.TeamsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class TeamController {

    private final TeamsRepository teamsRepository;

    public TeamController(TeamsRepository teamsRepository) {
        this.teamsRepository = teamsRepository;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // Anyone logged in should be able to see the team list
    public ResponseEntity<List<Teams>> getAllTeams() {
        return new ResponseEntity<>(teamsRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    // FIXED: Added the underscore!
    @PreAuthorize("hasAnyAuthority('ROLE_CLUB_ADMIN', 'ROLE_CLUBADMIN', 'ROLE_ADMIN')") 
    public ResponseEntity<Teams> createTeam(@RequestBody Teams team) {
        Teams savedTeam = teamsRepository.save(team);
        return new ResponseEntity<>(savedTeam, HttpStatus.CREATED);
    }
}