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
        createUser("Sanjay Vinod K", "sanjay@vitbhopal.ac.in", "23BCE11016", adminRole, null);
        logger.info("Created Global Admin");

        // 3. Create Teams
        createUser("Sujal", "sujal@vitbhopal.ac.in", "23BCE1001", leadRole, marketingTeam);
        createUser("Kush", "kush@vitbhopal.ac.in", "23BCE1002", coLeadRole, marketingTeam);

        createUser("Himanshu", "himanshu@vitbhopal.ac.in", "23BCE1003", leadRole, photographyTeam);
        createUser("Vedansh", "vedansh@vitbhopal.ac.in", "23BCE1004", coLeadRole, photographyTeam);

        createUser("Ronit", "ronit@vitbhopal.ac.in", "23BCE1005", leadRole, eventTeam);
        createUser("Apoorv", "apoorv@vitbhopal.ac.in", "23BCE1006", coLeadRole, eventTeam);

        // 4. Create Regular Members
        createUser("Marketer1", "m1@vitbhopal.ac.in", "23BCE1007", memberRole, marketingTeam);
        createUser("Marketer2", "m2@vitbhopal.ac.in", "23BCE1008", memberRole, marketingTeam);
        
        createUser("Photographer1", "p1@vitbhopal.ac.in", "23BCE1009", memberRole, photographyTeam);
        createUser("Photographer2", "p2@vitbhopal.ac.in", "23BCE1010", memberRole, photographyTeam);
        
        createUser("EventStaff1", "e1@vitbhopal.ac.in", "23BCE1011", memberRole, eventTeam);
        createUser("EventStaff2", "e2@vitbhopal.ac.in", "23BCE1012", memberRole, eventTeam);

        logger.info("Dummy data seeding complete!");
    }

    // Updated helper method to include the required fields
    private void createUser(String name, String email, String regNumber, Roles role, Teams team) {
        Users user = new Users();
        user.setUserName(name); 
        user.setEmail(email);
        user.setRegistrationNumber(regNumber);
        user.setPasswordHash("dummyPassword123"); // Hardcoded password for test users
        user.setRole(role); 
        user.setTeam(team); 
        usersRepository.save(user);
    }
}