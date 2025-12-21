package com.evoting.model;

public enum UserRole {
    ADMIN("Administrator"),
    SUPER_ADMIN("Super Administrator"),
    VOTER("Voter"),
    CANDIDATE("Candidate");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}