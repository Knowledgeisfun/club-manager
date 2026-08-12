package com.vit.club_manager.config;

import com.vit.club_manager.model.Channels;
import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.repository.ChannelsRepository;
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

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final RolesRepository rolesRepository;
    private final TeamsRepository teamsRepository;
    private final ChannelsRepository channelsRepository; // 1. Inject ChannelsRepository

    public DatabaseSeeder(RolesRepository rolesRepository, 
                          TeamsRepository teamsRepository, 
                          ChannelsRepository channelsRepository) {
        this.rolesRepository = rolesRepository;
        this.teamsRepository = teamsRepository;
        this.channelsRepository = channelsRepository;
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

        // 3. Seed Messaging Channels (Structural data, safe for production)
        seedChannels();
    }

    private void seedChannels() {
        // Global Announcements Channel
        if (channelsRepository.findByChannelName("Global Announcements").isEmpty()) {
            Channels global = new Channels();
            global.setChannelName("Global Announcements");
            global.setChannelType("GLOBAL");
            global.setTeam(null);
            channelsRepository.save(global);
            logger.info("Created Channel: Global Announcements");
        }

        // Leadership Lounge Channel
        if (channelsRepository.findByChannelName("Leadership Lounge").isEmpty()) {
            Channels leadership = new Channels();
            leadership.setChannelName("Leadership Lounge");
            leadership.setChannelType("LEADERSHIP");
            leadership.setTeam(null);
            channelsRepository.save(leadership);
            logger.info("Created Channel: Leadership Lounge");
        }

        // Team-Specific Channels
        seedTeamChannel("Marketing Chat", AppConstants.TEAM_MARKETING);
        seedTeamChannel("Photography Chat", AppConstants.TEAM_PHOTO);
        seedTeamChannel("Event Chat", AppConstants.TEAM_EVENT);
    }

    private void seedTeamChannel(String channelName, String teamName) {
        if (channelsRepository.findByChannelName(channelName).isEmpty()) {
            Teams team = teamsRepository.findByTeamName(teamName);
            if (team != null) {
                Channels channel = new Channels();
                channel.setChannelName(channelName);
                channel.setChannelType("TEAM");
                channel.setTeam(team);
                channelsRepository.save(channel);
                logger.info("Created Team Channel: {}", channelName);
            }
        }
    }
}