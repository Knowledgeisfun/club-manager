package com.vit.club_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    //gmail unique identifier
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "registration_number", unique = true, length = 20)
    private String registrationNumber;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Foreign Key: role_id
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    // Foreign Key: team_id
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Teams team;

    public Users() {}

    
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Roles getRole() { return role; }
    public void setRole(Roles role) { this.role = role; }

    public Teams getTeam() { return team; }
    public void setTeam(Teams team) { this.team = team; }
}