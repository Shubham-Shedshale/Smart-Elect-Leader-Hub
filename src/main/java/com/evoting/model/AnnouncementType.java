package com.evoting.model;

public enum AnnouncementType {
    ELECTION_SCHEDULE("Election Schedule"),
    CANDIDATE_ANNOUNCEMENT("Candidate Announcement"),
    VOTING_PROCEDURE("Voting Procedure"),
    RESULTS_ANNOUNCEMENT("Results Announcement"),
    GENERAL("General"),
    URGENT("Urgent");
    
    private final String displayName;
    
    AnnouncementType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}