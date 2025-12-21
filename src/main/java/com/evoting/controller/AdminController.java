package com.evoting.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.evoting.model.Announcement;
import com.evoting.model.AnnouncementType;
import com.evoting.model.Candidate;
import com.evoting.model.CandidateStatus;
import com.evoting.model.Election;
import com.evoting.model.User;
import com.evoting.model.UserRole;
import com.evoting.repository.UserRepository;
import com.evoting.service.AnnouncementService;
import com.evoting.service.CandidateService;
import com.evoting.service.ElectionService;
import com.evoting.service.EmailService;
import com.evoting.service.UserService;
import com.evoting.service.VoteService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
// Temporarily disabled security for testing
// @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {
    
    @Autowired
    private ElectionService electionService;
    
    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VoteService voteService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Election Management
    @PostMapping("/elections")
    public ResponseEntity<?> createElection(@Valid @RequestBody CreateElectionRequest request) {
        try {
            Election election = electionService.createElection(
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCreatedBy()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election created successfully",
                "election", election
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create election",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/elections")
    public ResponseEntity<?> getAllElections() {
        try {
            // Update election statuses based on current time before returning data
            electionService.updateElectionStatuses();

            List<Election> elections = electionService.getAllElections();

            // Add analytics data to each election for the reports table
            List<Map<String, Object>> electionsWithAnalytics = elections.stream().map(election -> {
                Map<String, Object> electionMap = new HashMap<>();
                electionMap.put("id", election.getId());
                electionMap.put("title", election.getTitle());
                electionMap.put("description", election.getDescription());
                electionMap.put("startDate", election.getStartDate());
                electionMap.put("endDate", election.getEndDate());
                electionMap.put("status", election.getStatus().name());
                electionMap.put("totalCandidates", election.getTotalCandidates());
                electionMap.put("createdAt", election.getCreatedAt());
                electionMap.put("updatedAt", election.getUpdatedAt());

                try {
                    // Get analytics data for leading candidate and vote counts
                    VoteService.VoteAnalytics analytics = voteService.getVoteAnalytics(election.getId());

                    // Get leading candidate
                    VoteService.VoteAnalytics.CandidateResult leadingCandidate = analytics.getLeadingCandidate();
                    String leadingCandidateName = leadingCandidate != null ? leadingCandidate.getCandidateName() : null;
                    int leadingCandidateVotes = leadingCandidate != null ? (int) leadingCandidate.getVotes() : 0;

                    // Add analytics data
                    electionMap.put("totalVotes", (int) analytics.getTotalVotes());
                    electionMap.put("leadingCandidate", leadingCandidateName != null ? leadingCandidateName : "N/A");
                    electionMap.put("leadingCandidateVotes", leadingCandidateVotes);

                    // Calculate participation rate (simplified - using active voters as denominator)
                    long activeVoters = userService.countActiveVoters();
                    double participationRate = activeVoters > 0 ? (double) analytics.getTotalVotes() / activeVoters * 100 : 0;
                    electionMap.put("participationRate", Math.min(100.0, participationRate));

                } catch (Exception e) {
                    // If analytics fail, provide defaults
                    electionMap.put("totalVotes", 0);
                    electionMap.put("leadingCandidate", "N/A");
                    electionMap.put("leadingCandidateVotes", 0);
                    electionMap.put("participationRate", 0.0);
                }

                return electionMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "elections", electionsWithAnalytics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch elections",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/elections/{id}")
    public ResponseEntity<?> getElectionById(@PathVariable Long id) {
        try {
            Optional<Election> election = electionService.getElectionById(id);
            if (election.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "election", election.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch election",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/elections/{id}")
    public ResponseEntity<?> updateElection(@PathVariable Long id, @Valid @RequestBody UpdateElectionRequest request) {
        try {
            Election election = electionService.updateElection(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election updated successfully",
                "election", election
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update election",
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/elections/{id}")
    public ResponseEntity<?> deleteElection(@PathVariable Long id) {
        try {
            electionService.deleteElection(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to delete election",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/elections/{id}/schedule")
    public ResponseEntity<?> scheduleElection(@PathVariable Long id) {
        try {
            Election election = electionService.scheduleElection(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election scheduled successfully",
                "election", election
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to schedule election",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/elections/{id}/activate")
    public ResponseEntity<?> activateElection(@PathVariable Long id) {
        try {
            Election election = electionService.activateElection(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election activated successfully",
                "election", election
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to activate election",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/elections/{id}/complete")
    public ResponseEntity<?> completeElection(@PathVariable Long id) {
        try {
            Election election = electionService.completeElection(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election completed successfully",
                "election", election
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to complete election",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/elections/active")
    public ResponseEntity<?> getActiveElections() {
        try {
            List<Election> elections = electionService.getActiveElections();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "elections", elections
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch active elections",
                "message", e.getMessage()
            ));
        }
    }

    // Temporary endpoint for testing - activate election without authentication
    @PostMapping("/test/activate/{id}")
    public ResponseEntity<?> testActivateElection(@PathVariable Long id) {
        try {
            System.out.println("DEBUG: testActivateElection called for ID: " + id);
            Election election = electionService.activateElection(id);
            System.out.println("DEBUG: Election activated: " + election.getTitle() + ", Status: " + election.getStatus());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election activated successfully for testing",
                "election", Map.of(
                    "id", election.getId(),
                    "title", election.getTitle(),
                    "status", election.getStatus()
                )
            ));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to activate election: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to activate election",
                "message", e.getMessage()
            ));
        }
    }

    // Temporary endpoint for testing email functionality
    @PostMapping("/test/send-email")
    public ResponseEntity<?> testSendEmail(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("to");
            String subject = request.getOrDefault("subject", "Test Email from Electronic Voting System");
            String content = request.getOrDefault("content", "This is a test email to verify SMTP configuration is working correctly.");

            if (toEmail == null || toEmail.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email address is required",
                    "message", "Please provide a 'to' email address"
                ));
            }

            // Use the email service to send the test email
            emailService.sendSingleEmail(toEmail, subject, content);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test email sent successfully",
                "to", toEmail,
                "subject", subject
            ));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to send test email: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to send test email",
                "message", e.getMessage()
            ));
        }
    }

    // Temporary endpoint for testing email functionality without authentication
    @PostMapping("/test/send-email-public")
    public ResponseEntity<?> testSendEmailPublic(@RequestBody Map<String, String> request) {
        try {
            String toEmail = request.get("to");
            String subject = request.getOrDefault("subject", "Test Email from Electronic Voting System");
            String content = request.getOrDefault("content", "This is a test email to verify SMTP configuration is working correctly.");

            if (toEmail == null || toEmail.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email address is required",
                    "message", "Please provide a 'to' email address"
                ));
            }

            // Use the email service to send the test email
            emailService.sendSingleEmail(toEmail, subject, content);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Test email sent successfully",
                "to", toEmail,
                "subject", subject
            ));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to send test email: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to send test email",
                "message", e.getMessage()
            ));
        }
    }
    
    // Fix timezone data for election
    @PostMapping("/fix-timezone/{id}")
    public ResponseEntity<?> fixElectionTimezone(@PathVariable Long id) {
        try {
            Optional<Election> electionOpt = electionService.getElectionById(id);
            if (electionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Election election = electionOpt.get();
            // Subtract 5.5 hours to correct the timezone issue
            election.setStartDate(election.getStartDate().minusHours(5).minusMinutes(30));
            election.setEndDate(election.getEndDate().minusHours(5).minusMinutes(30));
            
            Election updatedElection = electionService.saveElection(election);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Election timezone fixed",
                "election", updatedElection
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fix timezone",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/elections/upcoming")
    public ResponseEntity<?> getUpcomingElections() {
        try {
            List<Election> elections = electionService.getUpcomingElections();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "elections", elections
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch upcoming elections",
                "message", e.getMessage()
            ));
        }
    }
    
    // Candidate Management
    @PostMapping("/candidates")
    public ResponseEntity<?> createCandidate(@Valid @RequestBody CreateCandidateRequest request) {
        try {
            Candidate candidate = candidateService.createCandidate(
                request.getElectionId(),
                request.getFullName(),
                request.getPartyName(),
                request.getPosition(),
                request.getCreatedBy()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate created successfully",
                "candidate", candidate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create candidate",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/elections/{electionId}/candidates")
    public ResponseEntity<?> getCandidatesByElection(@PathVariable Long electionId) {
        try {
            List<Candidate> candidates = candidateService.getCandidatesByElectionId(electionId);
            
            List<Map<String, Object>> candidateDTOs = candidates.stream().map(c -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", c.getId());
                dto.put("fullName", c.getFullName());
                dto.put("partyName", c.getPartyName());
                dto.put("position", c.getPosition());
                dto.put("bio", c.getBio());
                dto.put("status", c.getStatus());
                dto.put("profileImage", c.getProfileImage());
                dto.put("votesCount", c.getVotesCount());
                dto.put("createdAt", c.getCreatedAt());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "candidates", candidateDTOs
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch candidates",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/candidates/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable Long id, @Valid @RequestBody UpdateCandidateRequest request) {
        try {
            Candidate candidate = candidateService.updateCandidate(
                id,
                request.getFullName(),
                request.getPartyName(),
                request.getPosition(),
                request.getBio(),
                request.getContactNumber(),
                request.getEmailAddress(),
                request.getWebsiteUrl(),
                request.getManifesto()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate updated successfully",
                "candidate", candidate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update candidate",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/candidates/{id}/status")
    public ResponseEntity<?> updateCandidateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            CandidateStatus status = CandidateStatus.valueOf(request.get("status"));
            Candidate candidate = candidateService.updateCandidateStatus(id, status);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate status updated successfully",
                "candidate", candidate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update candidate status",
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        try {
            candidateService.deleteCandidate(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Candidate deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to delete candidate",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/candidates/{id}/statistics")
    public ResponseEntity<?> getCandidateStatistics(@PathVariable Long id) {
        try {
            CandidateService.CandidateStatistics stats = candidateService.getCandidateStatistics(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "statistics", stats
            ));
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load candidate statistics for candidate " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch candidate statistics",
                "message", e.getMessage()
            ));
        }
    }
    
    // Announcement Management
    @PostMapping("/announcements")
    public ResponseEntity<?> createAnnouncement(@Valid @RequestBody CreateAnnouncementRequest request) {
        try {
            AnnouncementType type = AnnouncementType.valueOf(request.getType());
            Announcement announcement = announcementService.createAnnouncement(
                request.getElectionId(),
                request.getTitle(),
                request.getContent(),
                type,
                request.getCreatedBy()
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Announcement created successfully",
                "announcement", announcement
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create announcement",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/announcements/{id}/publish")
    public ResponseEntity<?> publishAnnouncement(@PathVariable Long id) {
        try {
            Announcement announcement = announcementService.publishAnnouncement(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Announcement published successfully",
                "announcement", announcement
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to publish announcement",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/announcements")
    public ResponseEntity<?> getAllAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getPublishedAnnouncements();
            
            List<Map<String, Object>> announcementDTOs = announcements.stream().map(a -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", a.getId());
                dto.put("title", a.getTitle());
                dto.put("content", a.getContent());
                dto.put("type", a.getType());
                dto.put("priority", a.getPriority());
                dto.put("isPublished", a.getIsPublished());
                dto.put("publishedAt", a.getPublishedAt());
                dto.put("createdAt", a.getCreatedAt());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "announcements", announcementDTOs
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch announcements",
                "message", e.getMessage()
            ));
        }
    }
    
    // Bulk Email Import
    @PostMapping("/voters/import")
    public ResponseEntity<?> importVoters(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "File is empty",
                    "message", "Please select a file to upload"
                ));
            }
            
            // Implementation for Excel file parsing would go here
            // This is a placeholder response
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Voter import functionality will be implemented",
                "filename", file.getOriginalFilename(),
                "size", file.getSize()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to import voters",
                "message", e.getMessage()
            ));
        }
    }
    
    // Analytics and Reports
    @GetMapping("/analytics/elections/{electionId}")
    public ResponseEntity<?> getElectionAnalytics(@PathVariable Long electionId) {
        try {
            VoteService.VoteAnalytics analytics = voteService.getVoteAnalytics(electionId);

            // Get additional election data
            Optional<Election> electionOpt = electionService.getElectionById(electionId);
            if (electionOpt.isPresent()) {
                Election election = electionOpt.get();
                long activeVoters = userService.countActiveVoters();
                double participationRate = analytics.getVotingParticipationRate(activeVoters);

                // Get leading candidate name
                VoteService.VoteAnalytics.CandidateResult leadingCandidate = analytics.getLeadingCandidate();
                String leadingCandidateName = leadingCandidate != null ? leadingCandidate.getCandidateName() : null;
                long leadingCandidateVotes = leadingCandidate != null ? leadingCandidate.getVotes() : 0;

                // Get hourly voting activity (last 24 hours)
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime yesterday = now.minusDays(1);
                List<Object[]> hourlyActivity = voteService.getVoteActivityByHour(electionId, yesterday, now);

                // Convert to hourly data (24 hours, 0-23)
                int[] hourlyVotes = new int[24];
                for (Object[] hourData : hourlyActivity) {
                    Integer hour = ((Integer) hourData[0]);
                    Long count = (Long) hourData[1];
                    if (hour >= 0 && hour < 24) {
                        hourlyVotes[hour] = count.intValue();
                    }
                }

                // Get voting trends by day (last 30 days for the selected election)
                LocalDateTime electionStart = election.getStartDate();
                LocalDateTime electionEnd = election.getEndDate();
                LocalDateTime trendsStart = electionStart.isAfter(now.minusDays(30)) ? electionStart : now.minusDays(30);
                LocalDateTime trendsEnd = electionEnd.isBefore(now) ? electionEnd : now;

                List<Object[]> dailyTrends = voteService.getVoteTrendsByDay(electionId, trendsStart, trendsEnd);

                // Convert to daily trend data
                List<Map<String, Object>> dailyVoteData = dailyTrends.stream().map(dayData -> {
                    java.sql.Date date = (java.sql.Date) dayData[0];
                    Long count = (Long) dayData[1];
                    Map<String, Object> dayEntry = new HashMap<>();
                    dayEntry.put("date", date.toString());
                    dayEntry.put("votes", count.intValue());
                    return dayEntry;
                }).collect(Collectors.toList());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("analytics", Map.of(
                    "totalVotes", analytics.getTotalVotes(),
                    "leadingCandidate", leadingCandidateName,
                    "leadingCandidateVotes", leadingCandidateVotes,
                    "results", analytics.getResults(),
                    "hourlyActivity", hourlyVotes,
                    "dailyTrends", dailyVoteData
                ));
                response.put("electionInfo", Map.of(
                    "totalCandidates", election.getTotalCandidates(),
                    "participationRate", participationRate,
                    "status", election.getStatus().name()
                ));

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch analytics",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalElections", electionService.getAllElections().size());
            stats.put("activeElections", electionService.getActiveElectionCount());
            stats.put("upcomingElections", electionService.getUpcomingElectionCount());
            stats.put("completedElections", electionService.getCompletedElectionCount());
            stats.put("totalVoters", userService.countActiveVoters());
            stats.put("recentActiveUsers", userService.getRecentlyActiveUsers().size());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch dashboard stats",
                "message", e.getMessage()
            ));
        }
    }
    
    // User Management
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String role, 
                                         @RequestParam(required = false) Boolean active,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "50") int size) {
        try {
            List<User> users;
            if (role != null) {
                UserRole userRole = UserRole.valueOf(role.toUpperCase());
                users = userRepository.findByRole(userRole);
            } else {
                users = userRepository.findAll();
            }
            
            // Filter by active status if specified
            if (active != null) {
                users = users.stream()
                    .filter(u -> u.getIsActive() == active)
                    .collect(Collectors.toList());
            }
            
            // Sort by creation date (newest first)
            users.sort((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()));
            
            // Simple pagination
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, users.size());
            List<User> pagedUsers = users.subList(Math.min(startIndex, users.size()), endIndex);
            
            // Create lightweight user DTOs to reduce response size
            List<Map<String, Object>> userDTOs = pagedUsers.stream().map(user -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", user.getId());
                dto.put("email", user.getEmail());
                dto.put("fullName", user.getFullName());
                dto.put("firstName", user.getFirstName());
                dto.put("lastName", user.getLastName());
                dto.put("role", user.getRole());
                dto.put("isActive", user.getIsActive());
                dto.put("isVerified", user.getIsVerified());
                dto.put("createdAt", user.getCreatedAt());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "users", userDTOs,
                "totalCount", users.size(),
                "page", page,
                "size", size
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch users",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/users/voters")
    public ResponseEntity<?> getVoters() {
        try {
            List<User> voters = userService.getVoters();
            
            List<Map<String, Object>> voterDTOs = voters.stream().map(v -> {
                Map<String, Object> dto = new HashMap<>();
                dto.put("id", v.getId());
                dto.put("email", v.getEmail());
                dto.put("fullName", v.getFullName());
                dto.put("isActive", v.getIsActive());
                dto.put("isVerified", v.getIsVerified());
                dto.put("createdAt", v.getCreatedAt());
                return dto;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "voters", voterDTOs
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch voters",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/users/admins")
    public ResponseEntity<?> getAdmins() {
        try {
            List<User> admins = userRepository.findByRole(UserRole.ADMIN);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "admins", admins
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch admins",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
            User user = userService.createUser(request.getEmail(), request.getPassword(), role);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User created successfully",
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to create user",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            // Update email if provided
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                user.setEmail(request.getEmail());
            }

            // Update role if provided
            if (request.getRole() != null) {
                UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
                user.setRole(role);
            }
            
            User updatedUser = userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User updated successfully",
                "user", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to update user",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User activated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to activate user",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deactivated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to deactivate user",
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            // Don't allow deleting the last admin
            if (user.getRole() == UserRole.ADMIN) {
                long adminCount = userRepository.findByRole(UserRole.ADMIN).size();
                if (adminCount <= 1) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "Cannot delete the last admin user",
                        "message", "At least one admin must remain in the system"
                    ));
                }
            }
            
            userRepository.delete(user);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to delete user",
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "New password is required",
                    "message", "Please provide a new password"
                ));
            }

            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password reset successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to reset password",
                "message", e.getMessage()
            ));
        }
    }
    
    // Request DTOs
    public static class CreateElectionRequest {
        @NotBlank
        private String title;
        private String description;
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startDate;
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endDate;
        @NotNull
        private Long createdBy;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        public Long getCreatedBy() { return createdBy; }
        public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    }
    
    public static class UpdateElectionRequest {
        @NotBlank
        private String title;
        private String description;
        @NotNull
        private LocalDateTime startDate;
        @NotNull
        private LocalDateTime endDate;
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }
    
    public static class CreateCandidateRequest {
        @NotNull
        private Long electionId;
        @NotBlank
        private String fullName;
        private String partyName;
        private String position;
        @NotNull
        private Long createdBy;
        
        // Getters and Setters
        public Long getElectionId() { return electionId; }
        public void setElectionId(Long electionId) { this.electionId = electionId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPartyName() { return partyName; }
        public void setPartyName(String partyName) { this.partyName = partyName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public Long getCreatedBy() { return createdBy; }
        public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    }
    
    public static class UpdateCandidateRequest {
        @NotBlank
        private String fullName;
        private String partyName;
        private String position;
        private String bio;
        private String contactNumber;
        private String emailAddress;
        private String websiteUrl;
        private String manifesto;
        
        // Getters and Setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPartyName() { return partyName; }
        public void setPartyName(String partyName) { this.partyName = partyName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        public String getEmailAddress() { return emailAddress; }
        public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
        public String getWebsiteUrl() { return websiteUrl; }
        public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
        public String getManifesto() { return manifesto; }
        public void setManifesto(String manifesto) { this.manifesto = manifesto; }
    }
    
    public static class CreateAnnouncementRequest {
        @NotNull
        private Long electionId;
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        @NotBlank
        private String type; // Will be converted to AnnouncementType
        @NotNull
        private Long createdBy;
        
        // Getters and Setters
        public Long getElectionId() { return electionId; }
        public void setElectionId(Long electionId) { this.electionId = electionId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getCreatedBy() { return createdBy; }
        public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    }
    
    // User Management Request DTOs
    public static class CreateUserRequest {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String role; // VOTER, ADMIN, SUPER_ADMIN
        private String firstName;
        private String lastName;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }
    
    public static class UpdateUserRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String role; // Optional - only update if provided

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
