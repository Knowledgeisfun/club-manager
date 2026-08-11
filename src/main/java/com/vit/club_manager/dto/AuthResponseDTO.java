package com.vit.club_manager.dto;

public class AuthResponseDTO {
    private String token;

    public AuthResponseDTO(String token) { this.token = token; }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}