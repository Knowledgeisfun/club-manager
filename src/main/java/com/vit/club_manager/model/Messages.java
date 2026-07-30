package com.vit.club_manager.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @Column(nullable = false, length = 250)
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    // Foreign Key: sender_id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users sender;

    // Foreign Key: team_id (Corrected mapped reference)
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Teams team;

    // Foreign Key: role_id (Corrected mapped reference)
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    public Messages() {}

    // Getters and Setters
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public Users getSender() { return sender; }
    public void setSender(Users sender) { this.sender = sender; }

    public Teams getTeam() { return team; }
    public void setTeam(Teams team) { this.team = team; }

    public Roles getRole() { return role; }
    public void setRole(Roles role) { this.role = role; }
}