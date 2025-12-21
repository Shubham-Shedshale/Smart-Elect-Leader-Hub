package com.evoting.model;

public enum ElectionStatus {
    DRAFT("Draft"),
    SCHEDULED("Scheduled"),
    ACTIVE("Active"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");
    
    private final String displayName;
    
    ElectionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}