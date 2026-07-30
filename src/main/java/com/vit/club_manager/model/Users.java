package com.vit.club_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String userName;

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

    // Getters and Setters
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Roles getRole() { return role; }
    public void setRole(Roles role) { this.role = role; }

    public Teams getTeam() { return team; }
    public void setTeam(Teams team) { this.team = team; }
}