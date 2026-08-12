package com.vit.club_manager.model;

import jakarta.persistence.*;

@Entity
@Table(name = "channels")
public class Channels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Integer channelId;

    @Column(nullable = false, unique = true, length = 100)
    private String channelName; // e.g., "Global Announcements", "Marketing Team", "Leadership Lounge"

    @Column(nullable = false, length = 50)
    private String channelType; // "GLOBAL", "TEAM", or "LEADERSHIP"

    // Nullable: Only used if channelType is "TEAM" so we know which team it belongs to
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = true)
    private Teams team;

    public Channels() {}

    // Getters and Setters
    public Integer getChannelId() { return channelId; }
    public void setChannelId(Integer channelId) { this.channelId = channelId; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }

    public Teams getTeam() { return team; }
    public void setTeam(Teams team) { this.team = team; }
}