package com.vit.club_manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MessageRequestDTO {

    @NotBlank(message = "Message content cannot be blank")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}