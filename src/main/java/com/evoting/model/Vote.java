package com.evoting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "votes", indexes = {
    @Index(name = "idx_vote_user", columnList = "userId"),
    @Index(name = "idx_vote_election", columnList = "electionId"),
    @Index(name = "idx_vote_candidate", columnList = "candidateId"),
    @Index(name = "idx_vote_timestamp", columnList = "votedAt")
})
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull
    @Column(name = "election_id", nullable = false)
    private Long electionId;
    
    @NotNull
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @CreationTimestamp
    @Column(name = "voted_at", updatable = false)
    private LocalDateTime votedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", insertable = false, updatable = false)
    private Election election;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private Candidate candidate;
    
    // Constructors
    public Vote() {}
    
    public Vote(Long userId, Long electionId, Long candidateId) {
        this.userId = userId;
        this.electionId = electionId;
        this.candidateId = candidateId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getElectionId() {
        return electionId;
    }
    
    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }
    
    public Long getCandidateId() {
        return candidateId;
    }
    
    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public LocalDateTime getVotedAt() {
        return votedAt;
    }
    
    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Election getElection() {
        return election;
    }
    
    public void setElection(Election election) {
        this.election = election;
    }
    
    public Candidate getCandidate() {
        return candidate;
    }
    
    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }
    
    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(userId, vote.userId) && 
               Objects.equals(electionId, vote.electionId) && 
               Objects.equals(candidateId, vote.candidateId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, electionId, candidateId);
    }
    
    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", userId=" + userId +
                ", electionId=" + electionId +
                ", candidateId=" + candidateId +
                ", votedAt=" + votedAt +
                '}';
    }
}