package com.vit.club_manager.dto;

import java.time.Instant;

public class ChatMessageDto {
    private Long channelId;
    private String senderUsername;
    private String senderRole;
    private String content;
    private Instant timestamp;
    private String messageType = "TEXT";
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
    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    // Add standard Getters and Setters here
}