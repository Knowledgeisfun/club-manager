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

    @Column(nullable = false, length = 500) // Slightly increased length for chat messages
    private String content;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    // Foreign Key: Who sent the message
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users sender;

    // Foreign Key: Which channel does this message belong to? (Global, Team, or Leadership)
    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    private Channels channel;

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

    public Channels getChannel() { return channel; }
    public void setChannel(Channels channel) { this.channel = channel; }
}