package com.vit.club_manager.config;

import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.repository.RolesRepository;
import com.vit.club_manager.repository.TeamsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

@Component
@Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    // Using the class-based Logger as per your established best practices
    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final RolesRepository rolesRepository;
    private final TeamsRepository teamsRepository;

    // Spring automatically injects the repositories here
    public DatabaseSeeder(RolesRepository rolesRepository, TeamsRepository teamsRepository) {
        this.rolesRepository = rolesRepository;
        this.teamsRepository = teamsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Seed Roles
        String[] defaultRoles = {
            AppConstants.ROLE_ADMIN, 
            AppConstants.ROLE_LEAD, 
            AppConstants.ROLE_CO_LEAD, 
            AppConstants.ROLE_MEMBER
        };
        
        for (String roleName : defaultRoles) {
            if (!rolesRepository.existsByRoleName(roleName)) {
                Roles role = new Roles();
                role.setRoleName(roleName);
                rolesRepository.save(role);
                logger.info("Created Role: {}", roleName);
            }
        }

        // 2. Seed Teams
       String[] defaultTeams = {
            AppConstants.TEAM_MARKETING, 
            AppConstants.TEAM_PHOTO, 
            AppConstants.TEAM_EVENT
        };
        
        for (String teamName : defaultTeams) {
            if (!teamsRepository.existsByTeamName(teamName)) {
                Teams team = new Teams();
                team.setTeamName(teamName);
                teamsRepository.save(team);
                logger.info("Created Team: {}", teamName);
            }
        }
    }
}