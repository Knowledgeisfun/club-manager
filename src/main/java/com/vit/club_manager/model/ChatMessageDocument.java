package com.vit.club_manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "chat_messages")
@CompoundIndex(name = "channel_time_idx", def = "{'channelId': 1, 'timestamp': -1}")
public class ChatMessageDocument {

    @Id
    private String id;
    private Long channelId;
    private String senderUsername;
    private String senderRole; // Added missing field
    private String content;
    private Instant timestamp = Instant.now();

    // 1. Default no-args constructor (Required by Spring Data MongoDB)
    public ChatMessageDocument() {
    }

    // 2. 4-argument constructor (Required by ChatMessageServiceImpl)
    public ChatMessageDocument(Long channelId, String senderUsername, String senderRole, String content) {
        this.channelId = channelId;
        this.senderUsername = senderUsername;
        this.senderRole = senderRole;
        this.content = content;
        this.timestamp = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}