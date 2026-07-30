package com.vit.club_manager.config;

import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.repository.RolesRepository;
import com.vit.club_manager.repository.TeamsRepository;
import com.vit.club_manager.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Runs SECOND, guaranteeing Roles and Teams already exist
public class DummyDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DummyDataSeeder.class);

    private final RolesRepository rolesRepository;
    private final TeamsRepository teamsRepository;
    private final UsersRepository usersRepository;

    public DummyDataSeeder(RolesRepository rolesRepository, TeamsRepository teamsRepository, UsersRepository usersRepository) {
        this.rolesRepository = rolesRepository;
        this.teamsRepository = teamsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // Stop if users already exist
        if (usersRepository.count() > 0) {
            logger.info("Dummy users already exist. Skipping seeding.");
            return; 
        }

        // 1. Fetch Lookups using AppConstants to avoid typos
        Roles adminRole = rolesRepository.findByRoleName(AppConstants.ROLE_ADMIN);
        Roles leadRole = rolesRepository.findByRoleName(AppConstants.ROLE_LEAD);
        Roles coLeadRole = rolesRepository.findByRoleName(AppConstants.ROLE_CO_LEAD);
        Roles memberRole = rolesRepository.findByRoleName(AppConstants.ROLE_MEMBER);

        Teams marketingTeam = teamsRepository.findByTeamName(AppConstants.TEAM_MARKETING);
        Teams photographyTeam = teamsRepository.findByTeamName(AppConstants.TEAM_PHOTO);
        Teams eventTeam = teamsRepository.findByTeamName(AppConstants.TEAM_EVENT);

        // 2. Create the Global Club Admin (No Team)
        createUser("Sanjay Vinod K", adminRole, null);
        logger.info("Created Global Admin");

        // 3. Create Teams
        createUser("Sujal", leadRole, marketingTeam);
        createUser("Kush", coLeadRole, marketingTeam);

        createUser("Himanshu", leadRole, photographyTeam);
        createUser("Vedansh", coLeadRole, photographyTeam);

        createUser("Ronit", leadRole, eventTeam);
        createUser("Apoorv", coLeadRole, eventTeam);

        // 4. Create Regular Members
        createUser("Marketer1", memberRole, marketingTeam);
        createUser("Marketer2", memberRole, marketingTeam);
        
        createUser("Photographer1", memberRole, photographyTeam);
        createUser("Photographer2", memberRole, photographyTeam);
        
        createUser("EventStaff1", memberRole, eventTeam);
        createUser("EventStaff2", memberRole, eventTeam);

        logger.info("Dummy data seeding complete!");
    }

    private void createUser(String name, Roles role, Teams team) {
        Users user = new Users();
        user.setUserName(name); 
        user.setRole(role); 
        user.setTeam(team); 
        usersRepository.save(user);
    }
}