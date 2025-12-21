package com.evoting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "candidates", indexes = {
    @Index(name = "idx_candidate_election", columnList = "electionId"),
    @Index(name = "idx_candidate_status", columnList = "status")
})
@JsonIgnoreProperties({"election", "votes"})
public class Candidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "election_id", nullable = false)
    private Long electionId;
    
    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(name = "party_name")
    private String partyName;
    
    @Column(name = "position")
    private String position;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "profile_image")
    private String profileImage;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "email_address")
    private String emailAddress;
    
    @Column(name = "website_url")
    private String websiteUrl;
    
    @Column(name = "manifesto", columnDefinition = "TEXT")
    private String manifesto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CandidateStatus status;
    
    @Column(name = "votes_count")
    private Long votesCount = 0L;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", insertable = false, updatable = false)
    private Election election;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Candidate() {}
    
    public Candidate(Long electionId, String fullName, String partyName, String position) {
        this.electionId = electionId;
        this.fullName = fullName;
        this.partyName = partyName;
        this.position = position;
        this.status = CandidateStatus.ACTIVE;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getElectionId() {
        return electionId;
    }
    
    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPartyName() {
        return partyName;
    }
    
    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfileImage() {
        return profileImage;
    }
    
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
    
    public String getManifesto() {
        return manifesto;
    }
    
    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }
    
    public CandidateStatus getStatus() {
        return status;
    }
    
    public void setStatus(CandidateStatus status) {
        this.status = status;
    }
    
    public Long getVotesCount() {
        return votesCount;
    }
    
    public void setVotesCount(Long votesCount) {
        this.votesCount = votesCount;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public Election getElection() {
        return election;
    }
    
    public void setElection(Election election) {
        this.election = election;
    }
    
    public List<Vote> getVotes() {
        return votes;
    }
    
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Utility methods
    public void incrementVotes() {
        this.votesCount = (this.votesCount != null ? this.votesCount : 0) + 1;
    }
    
    public void decrementVotes() {
        if (this.votesCount != null && this.votesCount > 0) {
            this.votesCount--;
        }
    }
    
    public boolean isActive() {
        return status == CandidateStatus.ACTIVE;
    }
    
    public String getDisplayName() {
        return partyName != null && !partyName.trim().isEmpty() 
               ? fullName + " (" + partyName + ")" 
               : fullName;
    }
}
