package com.evoting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.evoting.model.Candidate;
import com.evoting.model.Election;
import com.evoting.model.Vote;
import com.evoting.repository.CandidateRepository;
import com.evoting.repository.ElectionRepository;
import com.evoting.repository.VoteRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional
public class VoteService {
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private EmailService emailService;
    
    public Vote castVote(Long userId, Long electionId, Long candidateId) {
        // Verify the election is active
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            throw new RuntimeException("Election not found with id: " + electionId);
        }
        
        Election election = electionOpt.get();
        if (!election.isActive()) {
            throw new RuntimeException("Election is not currently active");
        }
        
        // Verify the candidate exists and belongs to this election
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isEmpty()) {
            throw new RuntimeException("Candidate not found with id: " + candidateId);
        }
        
        Candidate candidate = candidateOpt.get();
        if (!candidate.getElectionId().equals(electionId)) {
            throw new RuntimeException("Candidate does not belong to this election");
        }
        
        // Check if user has already voted in this election
        if (voteRepository.existsByUserIdAndElectionId(userId, electionId)) {
            throw new RuntimeException("User has already voted in this election");
        }
        
        // Check if election allows multiple candidates (for different positions)
        if (!election.getAllowMultipleCandidates()) {
            // Single candidate per election check already done above
        }
        
        // Create the vote
        Vote vote = new Vote(userId, electionId, candidateId);
        
        // Set vote metadata
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if (request != null) {
            vote.setIpAddress(getClientIpAddress(request));
            vote.setUserAgent(request.getHeader("User-Agent"));
            vote.setSessionId(request.getSession().getId());
        }
        vote.setVotedAt(LocalDateTime.now());
        
        // Save the vote
        Vote savedVote = voteRepository.save(vote);
        
        // Update candidate's vote count
        candidate.incrementVotes();
        candidateRepository.save(candidate);
        
        return savedVote;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    public Optional<Vote> getVoteByUserAndElection(Long userId, Long electionId) {
        return voteRepository.findByUserIdAndElectionId(userId, electionId);
    }
    
    public boolean hasUserVoted(Long userId, Long electionId) {
        return voteRepository.existsByUserIdAndElectionId(userId, electionId);
    }
    
    public List<Vote> getVotesByElectionId(Long electionId) {
        return voteRepository.findByElectionIdOrderByVotedAtDesc(electionId);
    }
    
    public List<Vote> getVotesByCandidateId(Long candidateId) {
        return voteRepository.findByCandidateIdOrderByVotedAtDesc(candidateId);
    }
    
    public List<Vote> getVotesByUserId(Long userId) {
        return voteRepository.findByUserIdOrderByVotedAtDesc(userId);
    }
    
    public long getTotalVotes(Long electionId) {
        return voteRepository.countByElectionId(electionId);
    }
    
    public long getVotesByCandidate(Long candidateId) {
        return voteRepository.countByCandidateId(candidateId);
    }
    
    public List<Object[]> getVoteCountsByElection(Long electionId) {
        return voteRepository.findVoteCountsByElectionId(electionId);
    }
    
    public List<Object[]> getVoteCountsByElectionAndDateRange(Long electionId, LocalDateTime startDate, LocalDateTime endDate) {
        return voteRepository.findVoteCountsByElectionIdAndDateRange(electionId, startDate, endDate);
    }

    public List<Object[]> getVoteActivityByHour(Long electionId, LocalDateTime startDate, LocalDateTime endDate) {
        return voteRepository.findVoteActivityByHour(electionId, startDate, endDate);
    }

    public List<Object[]> getVoteTrendsByDay(Long electionId, LocalDateTime startDate, LocalDateTime endDate) {
        return voteRepository.findVoteTrendsByDay(electionId, startDate, endDate);
    }
    
    public long getVotesToday(Long electionId) {
        // TODO: Fix the query for counting votes today
        // return voteRepository.countVotesTodayByElectionId(electionId);
        return 0; // Temporary implementation
    }
    
    public long getVotesInTimeRange(Long electionId, LocalDateTime startDate, LocalDateTime endDate) {
        return voteRepository.countVotesInTimeRange(electionId, startDate, endDate);
    }
    
    // Get vote analytics for an election
    public VoteAnalytics getVoteAnalytics(Long electionId) {
        VoteAnalytics analytics = new VoteAnalytics();
        
        long totalVotes = getTotalVotes(electionId);
        analytics.setTotalVotes(totalVotes);
        
        // Get candidate vote counts
        List<Object[]> voteCounts = getVoteCountsByElection(electionId);
        
        for (Object[] count : voteCounts) {
            Long candidateId = (Long) count[0];
            Long votes = (Long) count[1];
            
            Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
            if (candidateOpt.isPresent()) {
                Candidate candidate = candidateOpt.get();
                double percentage = totalVotes > 0 ? (double) votes / totalVotes * 100 : 0;
                
                analytics.addCandidateResult(candidate.getFullName(), votes, percentage);
            }
        }
        
        return analytics;
    }
    
    public boolean canUserVote(Long userId, Long electionId) {
        // Check if user has already voted
        if (hasUserVoted(userId, electionId)) {
            return false;
        }
        
        // Check if election is active
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        if (electionOpt.isEmpty()) {
            return false;
        }
        
        return electionOpt.get().isActive();
    }
    
    public void removeVote(Long voteId) {
        Optional<Vote> voteOpt = voteRepository.findById(voteId);
        if (voteOpt.isPresent()) {
            Vote vote = voteOpt.get();
            
            // Decrease candidate vote count
            Optional<Candidate> candidateOpt = candidateRepository.findById(vote.getCandidateId());
            if (candidateOpt.isPresent()) {
                Candidate candidate = candidateOpt.get();
                candidate.decrementVotes();
                candidateRepository.save(candidate);
            }
            
            // Remove the vote
            voteRepository.deleteById(voteId);
        }
    }
    
    // Inner class for vote analytics
    public static class VoteAnalytics {
        private long totalVotes;
        private List<CandidateResult> results;

        public VoteAnalytics() {
            this.results = new ArrayList<>();
        }

        public static class CandidateResult {
            private String candidateName;
            private long votes;
            private double percentage;

            public CandidateResult(String candidateName, long votes, double percentage) {
                this.candidateName = candidateName;
                this.votes = votes;
                this.percentage = percentage;
            }

            // Getters and Setters
            public String getCandidateName() {
                return candidateName;
            }

            public void setCandidateName(String candidateName) {
                this.candidateName = candidateName;
            }

            public long getVotes() {
                return votes;
            }

            public void setVotes(long votes) {
                this.votes = votes;
            }

            public double getPercentage() {
                return percentage;
            }

            public void setPercentage(double percentage) {
                this.percentage = percentage;
            }
        }

        public long getTotalVotes() {
            return totalVotes;
        }

        public void setTotalVotes(long totalVotes) {
            this.totalVotes = totalVotes;
        }

        public List<CandidateResult> getResults() {
            return results;
        }

        public void setResults(List<CandidateResult> results) {
            this.results = results;
        }

        public void addCandidateResult(String candidateName, long votes, double percentage) {
            if (this.results == null) {
                this.results = new ArrayList<>();
            }
            this.results.add(new CandidateResult(candidateName, votes, percentage));
        }

        public CandidateResult getLeadingCandidate() {
            if (results != null && !results.isEmpty()) {
                return results.stream()
                    .max((a, b) -> Long.compare(a.getVotes(), b.getVotes()))
                    .orElse(null);
            }
            return null;
        }

        public double getVotingParticipationRate(long eligibleVoters) {
            return eligibleVoters > 0 ? (double) totalVotes / eligibleVoters * 100 : 0;
        }
    }
}
