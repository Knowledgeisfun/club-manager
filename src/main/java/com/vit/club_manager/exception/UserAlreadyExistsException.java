package com.vit.club_manager.exception;

public class UserAlreadyExistsException extends RuntimeException {
    
    private final String details; // Add this field

    // Constructor now takes BOTH the message and the details
    public UserAlreadyExistsException(String message, String details) {
        super(message);
        this.details = details;
    }

    public String getDetails() {
        return details;
    }
}