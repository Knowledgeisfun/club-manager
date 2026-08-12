package com.vit.club_manager.config;

import com.vit.club_manager.model.Channels;
import com.vit.club_manager.model.Messages;
import com.vit.club_manager.model.Roles;
import com.vit.club_manager.model.Teams;
import com.vit.club_manager.model.Users;
import com.vit.club_manager.repository.ChannelsRepository;
import com.vit.club_manager.repository.MessagesRepository;
import com.vit.club_manager.repository.RolesRepository;
import com.vit.club_manager.repository.TeamsRepository;
import com.vit.club_manager.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@Order(2) // Runs SECOND, guaranteeing Roles, Teams, and Channels exist from Order(1)
public class DummyDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DummyDataSeeder.class);

    private final RolesRepository rolesRepository;
    private final TeamsRepository teamsRepository;
    private final UsersRepository usersRepository;
    private final ChannelsRepository channelsRepository;
    private final MessagesRepository messagesRepository;
    private final PasswordEncoder passwordEncoder;

    public DummyDataSeeder(RolesRepository rolesRepository, 
                           TeamsRepository teamsRepository, 
                           UsersRepository usersRepository, 
                           ChannelsRepository channelsRepository,
                           MessagesRepository messagesRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolesRepository = rolesRepository;
        this.teamsRepository = teamsRepository;
        this.usersRepository = usersRepository;
        this.channelsRepository = channelsRepository;
        this.messagesRepository = messagesRepository;
        this.passwordEncoder = passwordEncoder;
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
        Users admin = createUser("Sanjay Vinod K", "sanjay@vitbhopal.ac.in", "23BCE11016", adminRole, null);
        logger.info("Created Global Admin");

        // 3. Create Teams
        Users sujal = createUser("Sujal", "sujal@vitbhopal.ac.in", "23BCE1001", leadRole, marketingTeam);
        Users kush = createUser("Kush", "kush@vitbhopal.ac.in", "23BCE1002", coLeadRole, marketingTeam);

        createUser("Himanshu", "himanshu@vitbhopal.ac.in", "23BCE1003", leadRole, photographyTeam);
        createUser("Vedansh", "vedansh@vitbhopal.ac.in", "23BCE1004", coLeadRole, photographyTeam);

        createUser("Ronit", "ronit@vitbhopal.ac.in", "23BCE1005", leadRole, eventTeam);
        createUser("Apoorv", "apoorv@vitbhopal.ac.in", "23BCE1006", coLeadRole, eventTeam);

        // 4. Create Regular Members
        Users marketer1 = createUser("Marketer1", "m1@vitbhopal.ac.in", "23BCE1007", memberRole, marketingTeam);
        createUser("Marketer2", "m2@vitbhopal.ac.in", "23BCE1008", memberRole, marketingTeam);
        
        createUser("Photographer1", "p1@vitbhopal.ac.in", "23BCE1009", memberRole, photographyTeam);
        createUser("Photographer2", "p2@vitbhopal.ac.in", "23BCE1010", memberRole, photographyTeam);
        
        createUser("EventStaff1", "e1@vitbhopal.ac.in", "23BCE1011", memberRole, eventTeam);
        createUser("EventStaff2", "e2@vitbhopal.ac.in", "23BCE1012", memberRole, eventTeam);

        // 5. Seed Initial Chat Messages for Visualization
        seedDummyMessages(admin, sujal, marketer1);

        logger.info("Dummy data and messages seeding complete!");
    }

    private Users createUser(String name, String email, String regNumber, Roles role, Teams team) {
        Users user = new Users();
        user.setUserName(name); 
        user.setEmail(email);
        user.setRegistrationNumber(regNumber);
        user.setPasswordHash(passwordEncoder.encode("admin123"));
        user.setRole(role); 
        user.setTeam(team); 
        user.setRequiresPasswordChange(false);
        return usersRepository.save(user);
    }

    private void seedDummyMessages(Users admin, Users sujal, Users marketer1) {
        // Fetch channels created by DatabaseSeeder
        Channels globalChannel = channelsRepository.findByChannelName("Global Announcements").orElse(null);
        Channels marketingChannel = channelsRepository.findByChannelName("Marketing Chat").orElse(null);
        Channels leadershipChannel = channelsRepository.findByChannelName("Leadership Lounge").orElse(null);

        // Drop some global messages
        if (globalChannel != null) {
            saveMessage(globalChannel, admin, "Welcome everyone to Club Manager 4.0! Keep an eye out for upcoming tasks.");
            saveMessage(globalChannel, admin, "Our first official meeting will take place this Friday at 5 PM.");
        }

        // Drop some marketing team messages
        if (marketingChannel != null) {
            saveMessage(marketingChannel, sujal, "Hey team, we need the posters for Gameflix 4.0 finalized by tomorrow.");
            saveMessage(marketingChannel, marketer1, "Got it! Working on the typography layouts right now.");
        }

        // Drop some leadership lounge messages
        if (leadershipChannel != null) {
            saveMessage(leadershipChannel, admin, "Leads, please ensure your team rosters are updated before the audit.");
            saveMessage(leadershipChannel, sujal, "Marketing roster is fully locked in, Admin!");
        }
    }

    private void saveMessage(Channels channel, Users sender, String content) {
        if (sender != null) {
            Messages msg = new Messages();
            msg.setChannel(channel);
            msg.setSender(sender);
            msg.setContent(content);
            messagesRepository.save(msg);
        }
    }
}