package com.evoting.service;

import com.evoting.model.Candidate;
import com.evoting.model.CandidateStatus;
import com.evoting.repository.CandidateRepository;
import com.evoting.repository.ElectionRepository;
import com.evoting.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CandidateService {
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private ElectionRepository electionRepository;
    
    public Candidate createCandidate(Long electionId, String fullName, String partyName, String position, Long createdBy) {
        // Validate election exists and is active
        if (!electionRepository.existsById(electionId)) {
            throw new RuntimeException("Election not found with id: " + electionId);
        }
        
        // Check if candidate with same email already exists for this election
        if (candidateRepository.existsByElectionIdAndFullName(electionId, fullName)) {
            throw new RuntimeException("Candidate with name " + fullName + " already exists for this election");
        }
        
        Candidate candidate = new Candidate(electionId, fullName, partyName, position);
        candidate.setCreatedBy(createdBy);
        candidate.setStatus(CandidateStatus.ACTIVE);
        
        return candidateRepository.save(candidate);
    }
    
    public Optional<Candidate> getCandidateById(Long id) {
        return candidateRepository.findById(id);
    }
    
    public List<Candidate> getCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElectionId(electionId);
    }
    
    public List<Candidate> getActiveCandidatesByElectionId(Long electionId) {
        return candidateRepository.findByElectionIdAndStatus(electionId, CandidateStatus.ACTIVE);
    }
    
    public List<Candidate> getCandidatesByElectionIdOrderByVotesDesc(Long electionId) {
        return candidateRepository.findByElectionIdOrderByVotesCountDesc(electionId);
    }
    
    public List<Candidate> searchCandidates(Long electionId, String keyword) {
        return candidateRepository.findByElectionIdAndKeyword(electionId, keyword);
    }
    
    public Candidate updateCandidate(Long id, String fullName, String partyName, String position, 
                                   String bio, String contactNumber, String emailAddress, 
                                   String websiteUrl, String manifesto) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            
            // Check if the name change conflicts with another candidate
            if (!candidate.getFullName().equals(fullName) && 
                candidateRepository.existsByElectionIdAndFullName(candidate.getElectionId(), fullName)) {
                throw new RuntimeException("Candidate with name " + fullName + " already exists for this election");
            }
            
            candidate.setFullName(fullName);
            candidate.setPartyName(partyName);
            candidate.setPosition(position);
            candidate.setBio(bio);
            candidate.setContactNumber(contactNumber);
            candidate.setEmailAddress(emailAddress);
            candidate.setWebsiteUrl(websiteUrl);
            candidate.setManifesto(manifesto);
            
            return candidateRepository.save(candidate);
        }
        throw new RuntimeException("Candidate not found with id: " + id);
    }
    
    public Candidate updateCandidateStatus(Long id, CandidateStatus status) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(id);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();
            candidate.setStatus(status);
            return candidateRepository.save(candidate);
        }
        throw new RuntimeException("Candidate not found with id: " + id);
    }
    
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }
    
    public long countActiveCandidates(Long electionId) {
        return candidateRepository.countByElectionIdAndStatus(electionId, CandidateStatus.ACTIVE);
    }
    
    public boolean isCandidateActive(Long candidateId) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        return candidateOpt.map(Candidate::isActive).orElse(false);
    }
    
    public List<Candidate> getCandidatesByAdmin(Long adminId) {
        return candidateRepository.findByCreatedByOrderByCreatedAtDesc(adminId);
    }
    
    // Get top candidates by votes for an election
    public List<Candidate> getTopCandidates(Long electionId, int limit) {
        List<Candidate> allCandidates = candidateRepository.findByElectionIdAndStatusOrderByVotesCountDesc(
            electionId, CandidateStatus.ACTIVE);
        
        if (allCandidates.size() <= limit) {
            return allCandidates;
        }
        
        return allCandidates.subList(0, limit);
    }
    
    // Get candidate statistics
    public CandidateStatistics getCandidateStatistics(Long candidateId) {
        Optional<Candidate> candidateOpt = candidateRepository.findById(candidateId);
        if (candidateOpt.isPresent()) {
            Candidate candidate = candidateOpt.get();

            // Validate that candidate has a valid election ID
            if (candidate.getElectionId() == null) {
                throw new RuntimeException("Candidate " + candidateId + " is not associated with any election");
            }

            CandidateStatistics stats = new CandidateStatistics();
            stats.setCandidateId(candidateId);
            stats.setCandidateName(candidate.getFullName());
            stats.setTotalVotes(candidate.getVotesCount());
            stats.setElectionId(candidate.getElectionId());
            stats.setStatus(candidate.getStatus());

            try {
                // Get total votes for the election to calculate percentage
                long totalElectionVotes = voteRepository.countByElectionId(candidate.getElectionId());
                stats.setPercentage(totalElectionVotes > 0 ?
                    (double) candidate.getVotesCount() / totalElectionVotes * 100 : 0.0);
            } catch (Exception e) {
                // Handle any database query errors gracefully
                stats.setPercentage(0.0);
            }

            return stats;
        }
        throw new RuntimeException("Candidate not found with id: " + candidateId);
    }
    
    // Inner class for candidate statistics
    public static class CandidateStatistics {
        private Long candidateId;
        private String candidateName;
        private Long totalVotes;
        private double percentage;
        private Long electionId;
        private CandidateStatus status;
        
        // Getters and Setters
        public Long getCandidateId() {
            return candidateId;
        }
        
        public void setCandidateId(Long candidateId) {
            this.candidateId = candidateId;
        }
        
        public String getCandidateName() {
            return candidateName;
        }
        
        public void setCandidateName(String candidateName) {
            this.candidateName = candidateName;
        }
        
        public Long getTotalVotes() {
            return totalVotes;
        }
        
        public void setTotalVotes(Long totalVotes) {
            this.totalVotes = totalVotes;
        }
        
        public double getPercentage() {
            return percentage;
        }
        
        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }
        
        public Long getElectionId() {
            return electionId;
        }
        
        public void setElectionId(Long electionId) {
            this.electionId = electionId;
        }
        
        public CandidateStatus getStatus() {
            return status;
        }
        
        public void setStatus(CandidateStatus status) {
            this.status = status;
        }
    }
}
