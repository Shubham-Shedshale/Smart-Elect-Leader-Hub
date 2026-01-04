package com.evoting.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evoting.model.Announcement;
import com.evoting.model.Candidate;
import com.evoting.model.Election;
import com.evoting.model.Semester;
import com.evoting.model.User;
import com.evoting.model.Vote;
import com.evoting.service.AnnouncementService;
import com.evoting.service.CandidateService;
import com.evoting.service.ElectionService;
import com.evoting.service.UserService;
import com.evoting.service.VoteService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/voter")
@CrossOrigin(origins = "*")
public class VoterController {
    
    @Autowired
    private ElectionService electionService;
    
    @Autowired
    private CandidateService candidateService;
    
    @Autowired
    private VoteService voteService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserService userService;
    
    // Get active elections for voting (filtered by user's semester)
    @GetMapping("/elections/active")
    public ResponseEntity<?> getActiveElections() {
        try {
            System.out.println("DEBUG: getActiveElections called");
            List<Election> activeElections = electionService.getActiveElections();
            System.out.println("DEBUG: Found " + activeElections.size() + " active elections");

            // Get current user's semester
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final Semester userSemester;

            if (authentication != null && authentication.getPrincipal() instanceof com.evoting.security.CustomUserDetailsService.UserPrincipal) {
                com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal =
                    (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();

                // Get user's semester from database
                Optional<User> userOpt = userService.getUserById(userPrincipal.getId());
                userSemester = userOpt.isPresent() ? userOpt.get().getSemester() : null;

                // Get user's department as well
                String userDepartment = userOpt.get().getDepartment();

                // Filter elections by user's semester and department
                List<Election> filteredElections = activeElections.stream()
                    .filter(election ->
                        (election.getSemester() == null || userSemester == null || election.getSemester().equals(userSemester)) &&
                        (election.getDepartment() == null || userDepartment == null || election.getDepartment().equals(userDepartment)))
                    .collect(java.util.stream.Collectors.toList());

                System.out.println("DEBUG: Filtered to " + filteredElections.size() + " elections for user's semester");

                List<Map<String, Object>> enrichedElections = new ArrayList<>();
                for (Election election : filteredElections) {
                    Map<String, Object> electionData = new HashMap<>();
                    electionData.put("id", election.getId());
                    electionData.put("title", election.getTitle());
                    electionData.put("description", election.getDescription());
                    electionData.put("startDate", election.getStartDate());
                    electionData.put("endDate", election.getEndDate());
                    electionData.put("hasVoted", voteService.hasUserVoted(userPrincipal.getId(), election.getId()));
                    electionData.put("totalCandidates", candidateService.countActiveCandidates(election.getId()));

                    enrichedElections.add(electionData);
                }

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "elections", enrichedElections
                ));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "elections", activeElections
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch active elections",
                "message", e.getMessage()
            ));
        }
    }
    
    // Get election details with candidates for voting
    @GetMapping("/elections/{electionId}/vote")
    public ResponseEntity<?> getElectionForVoting(@PathVariable Long electionId) {
        try {
            // Verify user is logged in
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication required",
                    "message", "Please log in to vote"
                ));
            }
            
            com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal = 
                (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            
            // Check if election is active
            Optional<Election> electionOpt = electionService.getElectionById(electionId);
            if (electionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Election election = electionOpt.get();
            if (!election.isActive()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Election not active",
                    "message", "This election is not currently accepting votes"
                ));
            }
            
            // Check if user has already voted
            boolean hasVoted = voteService.hasUserVoted(userPrincipal.getId(), electionId);
            if (hasVoted) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Already voted",
                    "message", "You have already cast your vote in this election"
                ));
            }
            
            // Get candidates
            List<Candidate> candidates = candidateService.getActiveCandidatesByElectionId(electionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("election", Map.of(
                "id", election.getId(),
                "title", election.getTitle(),
                "description", election.getDescription(),
                "startDate", election.getStartDate(),
                "endDate", election.getEndDate(),
                "allowMultipleCandidates", election.getAllowMultipleCandidates(),
                "maxVotesPerUser", election.getMaxVotesPerUser()
            ));
            response.put("candidates", candidates);
            response.put("canVote", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch election data",
                "message", e.getMessage()
            ));
        }
    }
    
    // Cast a vote
    @PostMapping("/elections/{electionId}/vote")
    public ResponseEntity<?> castVote(@PathVariable Long electionId, @Valid @RequestBody CastVoteRequest request) {
        try {
            // Verify user is logged in
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication required",
                    "message", "Please log in to vote"
                ));
            }
            
            com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal = 
                (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            
            // Validate request
            if (request.getCandidateId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Candidate selection required",
                    "message", "Please select a candidate to vote for"
                ));
            }
            
            // Check if user can vote
            if (!voteService.canUserVote(userPrincipal.getId(), electionId)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Cannot vote",
                    "message", "You cannot cast a vote in this election"
                ));
            }
            
            // Cast the vote
            Vote vote = voteService.castVote(userPrincipal.getId(), electionId, request.getCandidateId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vote cast successfully",
                "voteId", vote.getId(),
                "votedAt", vote.getVotedAt()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to cast vote",
                "message", e.getMessage()
            ));
        }
    }
    
    // Get user's voting history
    @GetMapping("/votes/history")
    public ResponseEntity<?> getVotingHistory() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication required",
                    "message", "Please log in to view voting history"
                ));
            }
            
            com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal = 
                (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            
            List<Vote> votes = voteService.getVotesByUserId(userPrincipal.getId());
            
            List<Map<String, Object>> votingHistory = new ArrayList<>();
            for (Vote vote : votes) {
                Map<String, Object> voteData = new HashMap<>();
                voteData.put("id", vote.getId());
                voteData.put("electionId", vote.getElectionId());
                voteData.put("candidateId", vote.getCandidateId());
                voteData.put("votedAt", vote.getVotedAt());
                
                // Get election and candidate details
                Optional<Election> electionOpt = electionService.getElectionById(vote.getElectionId());
                Optional<Candidate> candidateOpt = candidateService.getCandidateById(vote.getCandidateId());
                
                if (electionOpt.isPresent()) {
                    voteData.put("electionTitle", electionOpt.get().getTitle());
                }
                
                if (candidateOpt.isPresent()) {
                    voteData.put("candidateName", candidateOpt.get().getFullName());
                    voteData.put("partyName", candidateOpt.get().getPartyName());
                }
                
                votingHistory.add(voteData);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "votingHistory", votingHistory
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch voting history",
                "message", e.getMessage()
            ));
        }
    }
    
    // Get active announcements
    @GetMapping("/announcements")
    public ResponseEntity<?> getActiveAnnouncements() {
        try {
            List<Announcement> announcements = announcementService.getPublishedAnnouncements();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "announcements", announcements
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch announcements",
                "message", e.getMessage()
            ));
        }
    }
    
    // Get upcoming elections (filtered by user's semester)
    @GetMapping("/elections/upcoming")
    public ResponseEntity<?> getUpcomingElections() {
        try {
            List<Election> upcomingElections = electionService.getUpcomingElections();

            // Get current user's semester
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final Semester userSemester;

            if (authentication != null && authentication.getPrincipal() instanceof com.evoting.security.CustomUserDetailsService.UserPrincipal) {
                com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal =
                    (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();

                // Get user's semester and department from database
                Optional<User> userOpt = userService.getUserById(userPrincipal.getId());
                userSemester = userOpt.isPresent() ? userOpt.get().getSemester() : null;
                String userDepartment = userOpt.isPresent() ? userOpt.get().getDepartment() : null;

                // Filter elections by user's semester and department
                List<Election> filteredElections = upcomingElections.stream()
                    .filter(election ->
                        (election.getSemester() == null || userSemester == null || election.getSemester().equals(userSemester)) &&
                        (election.getDepartment() == null || userDepartment == null || election.getDepartment().equals(userDepartment)))
                    .collect(java.util.stream.Collectors.toList());

                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "elections", filteredElections
                ));
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "elections", upcomingElections
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch upcoming elections",
                "message", e.getMessage()
            ));
        }
    }
    
    // Get completed elections
    @GetMapping("/elections/completed")
    public ResponseEntity<?> getCompletedElections() {
        try {
            List<Election> completedElections = electionService.getCompletedElections();

            // Enrich with voting status for current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Map<String, Object>> enrichedElections = new ArrayList<>();

            if (authentication != null && authentication.getPrincipal() instanceof com.evoting.security.CustomUserDetailsService.UserPrincipal) {
                com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal =
                    (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();

                for (Election election : completedElections) {
                    Map<String, Object> electionData = new HashMap<>();
                    electionData.put("id", election.getId());
                    electionData.put("title", election.getTitle());
                    electionData.put("description", election.getDescription());
                    electionData.put("startDate", election.getStartDate());
                    electionData.put("endDate", election.getEndDate());
                    electionData.put("status", election.getStatus().name());
                    electionData.put("hasVoted", voteService.hasUserVoted(userPrincipal.getId(), election.getId()));

                    // Get basic vote count
                    try {
                        VoteService.VoteAnalytics analytics = voteService.getVoteAnalytics(election.getId());
                        electionData.put("totalVotes", analytics.getTotalVotes());
                    } catch (Exception e) {
                        electionData.put("totalVotes", 0);
                    }

                    enrichedElections.add(electionData);
                }
            } else {
                // Fallback without user-specific data
                for (Election election : completedElections) {
                    Map<String, Object> electionData = new HashMap<>();
                    electionData.put("id", election.getId());
                    electionData.put("title", election.getTitle());
                    electionData.put("description", election.getDescription());
                    electionData.put("startDate", election.getStartDate());
                    electionData.put("endDate", election.getEndDate());
                    electionData.put("status", election.getStatus().name());
                    electionData.put("hasVoted", false);
                    electionData.put("totalVotes", 0);
                    enrichedElections.add(electionData);
                }
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "elections", enrichedElections
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch completed elections",
                "message", e.getMessage()
            ));
        }
    }

    // Get election results (for completed elections that have been published)
    @GetMapping("/elections/{electionId}/results")
    public ResponseEntity<?> getElectionResults(@PathVariable Long electionId) {
        try {
            Optional<Election> electionOpt = electionService.getElectionById(electionId);
            if (electionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Election election = electionOpt.get();
            if (!election.isCompleted()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Results not available",
                    "message", "Results are only available for completed elections"
                ));
            }

            if (election.getResultsPublished() == null || !election.getResultsPublished()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Results not published",
                    "message", "The results for this election have not been published yet"
                ));
            }

            // Get vote analytics
            VoteService.VoteAnalytics analytics = voteService.getVoteAnalytics(electionId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("election", Map.of(
                "id", election.getId(),
                "title", election.getTitle(),
                "description", election.getDescription(),
                "startDate", election.getStartDate(),
                "endDate", election.getEndDate(),
                "totalVotes", analytics.getTotalVotes()
            ));
            response.put("results", analytics.getResults());
            response.put("leadingCandidate", analytics.getLeadingCandidate());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch election results",
                "message", e.getMessage()
            ));
        }
    }

    // Get current user profile information
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication required",
                    "message", "Please log in to view profile"
                ));
            }

            com.evoting.security.CustomUserDetailsService.UserPrincipal userPrincipal =
                (com.evoting.security.CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();

            Optional<User> userOpt = userService.getUserById(userPrincipal.getId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("email", user.getEmail());
            profile.put("fullName", user.getFullName());
            profile.put("firstName", user.getFirstName());
            profile.put("lastName", user.getLastName());
            profile.put("role", user.getRole());
            profile.put("isActive", user.getIsActive());
            profile.put("isVerified", user.getIsVerified());
            profile.put("lastLogin", user.getLastLogin());
            profile.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "profile", profile
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch user profile",
                "message", e.getMessage()
            ));
        }
    }

    // Request DTOs
    public static class CastVoteRequest {
        @NotNull
        private Long candidateId;
        
        public Long getCandidateId() { return candidateId; }
        public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    }
}
