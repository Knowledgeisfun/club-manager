package com.vit.club_manager.dto;

public class AuthResponseDTO {
    private String token;
    private boolean requiresPasswordChange;

    public AuthResponseDTO(String token, boolean requiresPasswordChange) { 
        this.token = token; 
        this.requiresPasswordChange = requiresPasswordChange;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    // Note: For booleans, standard Java getter naming is "is..." instead of "get..."
    public boolean isRequiresPasswordChange() { return requiresPasswordChange; }
    public void setRequiresPasswordChange(boolean requiresPasswordChange) { this.requiresPasswordChange = requiresPasswordChange; }
}