package com.evoting.model;

public enum CandidateStatus {
    ACTIVE("Active"),
    WITHDRAWN("Withdrawn"),
    DISQUALIFIED("Disqualified"),
    PENDING("Pending Approval");
    
    private final String displayName;
    
    CandidateStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}