package com.vit.club_manager.controller;

import com.vit.club_manager.model.Teams;
import com.vit.club_manager.repository.TeamsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamsRepository teamsRepository;

    public TeamController(TeamsRepository teamsRepository) {
        this.teamsRepository = teamsRepository;
    }

    @GetMapping
    public ResponseEntity<List<Teams>> getAllTeams() {
        return new ResponseEntity<>(teamsRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<Teams> createTeam(@RequestBody Teams team) {
        Teams savedTeam = teamsRepository.save(team);
        return new ResponseEntity<>(savedTeam, HttpStatus.CREATED);
    }
}