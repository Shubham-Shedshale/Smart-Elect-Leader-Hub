package com.evoting.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "elections", indexes = {
    @Index(name = "idx_election_status", columnList = "status"),
    @Index(name = "idx_election_dates", columnList = "startDate, endDate")
})
public class Election {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionStatus status;
    
    @Column(name = "max_votes_per_user")
    private Integer maxVotesPerUser = 1;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = true;
    
    @Column(name = "allow_multiple_candidates")
    private Boolean allowMultipleCandidates = false;
    
    @Column(name = "created_by")
    private Long createdBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "semester")
    private Semester semester;

    @Column(name = "department")
    private String department;

    @Column(name = "results_published")
    private Boolean resultsPublished = false;

    @JsonIgnore
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Candidate> candidates;
    
    @JsonIgnore
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vote> votes;
    
    @JsonIgnore
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Announcement> announcements;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Election() {}
    
    public Election(String title, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = ElectionStatus.DRAFT;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public ElectionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ElectionStatus status) {
        this.status = status;
    }
    
    public Integer getMaxVotesPerUser() {
        return maxVotesPerUser;
    }
    
    public void setMaxVotesPerUser(Integer maxVotesPerUser) {
        this.maxVotesPerUser = maxVotesPerUser;
    }
    
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }
    
    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public Boolean getAllowMultipleCandidates() {
        return allowMultipleCandidates;
    }
    
    public void setAllowMultipleCandidates(Boolean allowMultipleCandidates) {
        this.allowMultipleCandidates = allowMultipleCandidates;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Boolean getResultsPublished() {
        return resultsPublished;
    }

    public void setResultsPublished(Boolean resultsPublished) {
        this.resultsPublished = resultsPublished;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
    
    public List<Vote> getVotes() {
        return votes;
    }
    
    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }
    
    public List<Announcement> getAnnouncements() {
        return announcements;
    }
    
    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
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
    public boolean isActive() {
        return status == ElectionStatus.ACTIVE && 
               LocalDateTime.now().isAfter(startDate) && 
               LocalDateTime.now().isBefore(endDate);
    }
    
    public boolean isUpcoming() {
        return status == ElectionStatus.SCHEDULED || 
               (status == ElectionStatus.DRAFT && startDate.isAfter(LocalDateTime.now()));
    }
    
    public boolean isCompleted() {
        return status == ElectionStatus.COMPLETED || 
               LocalDateTime.now().isAfter(endDate);
    }
    
    public long getTotalVotes() {
        return votes != null ? votes.size() : 0;
    }
    
    public long getTotalCandidates() {
        return candidates != null ? candidates.size() : 0;
    }
    
    public String getDuration() {
        if (startDate != null && endDate != null) {
            long hours = java.time.Duration.between(startDate, endDate).toHours();
            return hours + " hours";
        }
        return "N/A";
    }
}
