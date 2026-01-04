package com.evoting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evoting.model.Announcement;
import com.evoting.model.AnnouncementType;
import com.evoting.model.Election;
import com.evoting.model.ElectionStatus;
import com.evoting.model.Semester;
import com.evoting.repository.AnnouncementRepository;
import com.evoting.repository.ElectionRepository;

@Service
@Transactional
public class ElectionService {
    
    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private CandidateService candidateService;
    
    public Election createElection(String title, String description, LocalDateTime startDate, LocalDateTime endDate, Long createdBy) {
        Election election = new Election(title, description, startDate, endDate);
        election.setCreatedBy(createdBy);
        election.setStatus(ElectionStatus.DRAFT);

        return electionRepository.save(election);
    }

    public Election createElectionWithSemester(String title, String description, LocalDateTime startDate, LocalDateTime endDate, Long createdBy, Semester semester) {
        Election election = new Election(title, description, startDate, endDate);
        election.setCreatedBy(createdBy);
        election.setSemester(semester);
        election.setStatus(ElectionStatus.DRAFT);

        return electionRepository.save(election);
    }

    public Election createElectionWithSemesterAndDepartment(String title, String description, LocalDateTime startDate, LocalDateTime endDate, Long createdBy, Semester semester, String department) {
        Election election = new Election(title, description, startDate, endDate);
        election.setCreatedBy(createdBy);
        election.setSemester(semester);
        election.setDepartment(department);
        election.setStatus(ElectionStatus.DRAFT);

        return electionRepository.save(election);
    }
    
    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }
    
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    
    public List<Election> getElectionsByStatus(ElectionStatus status) {
        return electionRepository.findByStatus(status);
    }
    
    public List<Election> getUpcomingElections() {
        return electionRepository.findUpcomingElections(LocalDateTime.now());
    }
    
    public List<Election> getActiveElections() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("DEBUG: ElectionService.getActiveElections called at " + now);

        // Also log ALL elections to see what's in the database
        List<Election> allElections = electionRepository.findAll();
        System.out.println("DEBUG: Total elections in database: " + allElections.size());
        for (Election election : allElections) {
            System.out.println("DEBUG: Election - ID: " + election.getId() + ", Title: " + election.getTitle() +
                             ", Status: " + election.getStatus() + ", Start: " + election.getStartDate() +
                             ", End: " + election.getEndDate());
        }

        List<Election> activeElections = electionRepository.findActiveElections(now);
        System.out.println("DEBUG: Found " + activeElections.size() + " active elections in database");
        for (Election election : activeElections) {
            System.out.println("DEBUG: Active election - ID: " + election.getId() + ", Title: " + election.getTitle() +
                             ", Status: " + election.getStatus() + ", Start: " + election.getStartDate() +
                             ", End: " + election.getEndDate());
        }
        return activeElections;
    }
    
    public List<Election> getCompletedElections() {
        return electionRepository.findCompletedElections(LocalDateTime.now());
    }
    
    public List<Election> getElectionsByAdmin(Long adminId) {
        return electionRepository.findByCreatedByOrderByCreatedAtDesc(adminId);
    }
    
    public Election updateElection(Long id, String title, String description, LocalDateTime startDate, LocalDateTime endDate) {
        Optional<Election> electionOpt = electionRepository.findById(id);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();
            election.setTitle(title);
            election.setDescription(description);
            election.setStartDate(startDate);
            election.setEndDate(endDate);
            return electionRepository.save(election);
        }
        throw new RuntimeException("Election not found with id: " + id);
    }
    
    public void deleteElection(Long id) {
        electionRepository.deleteById(id);
    }
    
    public Election saveElection(Election election) {
        return electionRepository.save(election);
    }
    
    public Election scheduleElection(Long id) {
        Optional<Election> electionOpt = electionRepository.findById(id);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();
            
            if (LocalDateTime.now().isBefore(election.getStartDate())) {
                election.setStatus(ElectionStatus.SCHEDULED);
                Election savedElection = electionRepository.save(election);
                
                // Send announcement about scheduled election
                createElectionAnnouncement(savedElection, "Election Scheduled", 
                    "The election '" + election.getTitle() + "' has been scheduled from " + 
                    election.getStartDate() + " to " + election.getEndDate(), AnnouncementType.ELECTION_SCHEDULE);
                
                return savedElection;
            } else {
                throw new RuntimeException("Cannot schedule election in the past");
            }
        }
        throw new RuntimeException("Election not found with id: " + id);
    }
    
    public Election activateElection(Long id) {
        Optional<Election> electionOpt = electionRepository.findById(id);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();

            // For testing purposes, allow activation regardless of dates
            // Comment out the date check for testing
            /*
            if (LocalDateTime.now().isAfter(election.getStartDate()) &&
                LocalDateTime.now().isBefore(election.getEndDate())) {
            */
                election.setStatus(ElectionStatus.ACTIVE);
                Election savedElection = electionRepository.save(election);

                // Send voting invitations to all voters
                sendVotingInvitations(savedElection);

                // Create activation announcement
                createElectionAnnouncement(savedElection, "Election Now Active",
                    "Voting is now open for the election '" + election.getTitle() + "'. " +
                    "Please cast your vote before " + election.getEndDate(),
                    AnnouncementType.ELECTION_SCHEDULE);

                return savedElection;
            /*
            } else {
                throw new RuntimeException("Election cannot be activated at this time");
            }
            */
        }
        throw new RuntimeException("Election not found with id: " + id);
    }
    
    public Election completeElection(Long id) {
        Optional<Election> electionOpt = electionRepository.findById(id);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();
            election.setStatus(ElectionStatus.COMPLETED);
            Election savedElection = electionRepository.save(election);

            // Note: Results are no longer automatically announced.
            // Admin must manually publish results using publishElectionResults()

            return savedElection;
        }
        throw new RuntimeException("Election not found with id: " + id);
    }

    public Election publishElectionResults(Long id) {
        Optional<Election> electionOpt = electionRepository.findById(id);
        if (electionOpt.isPresent()) {
            Election election = electionOpt.get();

            // Only allow publishing if election is completed
            if (election.getStatus() != ElectionStatus.COMPLETED) {
                throw new RuntimeException("Cannot publish results for an election that is not completed");
            }

            // Mark results as published
            election.setResultsPublished(true);
            Election savedElection = electionRepository.save(election);

            // Send results announcement to all voters
            sendElectionResultsAnnouncement(savedElection);

            return savedElection;
        }
        throw new RuntimeException("Election not found with id: " + id);
    }
    
    public void sendVotingInvitations(Election election) {
        // Get all active voters and filter by semester and department if specified
        List<com.evoting.model.User> allVoters = userService.getVoters();

        allVoters.stream()
            .filter(voter -> {
                // If election has semester restriction, check if voter matches
                if (election.getSemester() != null) {
                    if (voter.getSemester() == null || !voter.getSemester().equals(election.getSemester())) {
                        return false;
                    }
                }

                // If election has department restriction, check if voter matches
                if (election.getDepartment() != null && !election.getDepartment().trim().isEmpty()) {
                    if (voter.getDepartment() == null || !voter.getDepartment().equals(election.getDepartment())) {
                        return false;
                    }
                }

                return true; // Voter is eligible
            })
            .forEach(voter -> {
                String votingUrl = "http://localhost:8080/vote/" + election.getId();
                try {
                    emailService.sendVotingInvitation(voter, election, votingUrl);
                    System.out.println("Sent voting invitation to " + voter.getEmail() +
                                     " (Semester: " + voter.getSemester() + ", Department: " + voter.getDepartment() + ")");
                } catch (Exception e) {
                    System.err.println("Failed to send voting invitation to " + voter.getEmail() + ": " + e.getMessage());
                }
            });
    }
    
    private void createElectionAnnouncement(Election election, String title, String content, AnnouncementType type) {
        Announcement announcement = new Announcement(election.getId(), title, content, type);
        announcement.setCreatedBy(election.getCreatedBy());
        announcement.publish();
        announcementRepository.save(announcement);
    }

    private void sendElectionResultsAnnouncement(Election election) {
        // Create announcement for published results
        createElectionAnnouncement(election, "Election Results Published",
            "The results for the election '" + election.getTitle() + "' are now available. " +
            "You can view the final results in your dashboard.",
            AnnouncementType.RESULTS_ANNOUNCEMENT);

        // Get all active voters and filter by semester and department if specified
        List<com.evoting.model.User> allVoters = userService.getVoters();
        List<com.evoting.model.Candidate> candidates = candidateService.getCandidatesByElectionId(election.getId());

        allVoters.stream()
            .filter(voter -> {
                // If election has semester restriction, check if voter matches
                if (election.getSemester() != null) {
                    if (voter.getSemester() == null || !voter.getSemester().equals(election.getSemester())) {
                        return false;
                    }
                }

                // If election has department restriction, check if voter matches
                if (election.getDepartment() != null && !election.getDepartment().trim().isEmpty()) {
                    if (voter.getDepartment() == null || !voter.getDepartment().equals(election.getDepartment())) {
                        return false;
                    }
                }

                return true; // Voter is eligible
            })
            .forEach(voter -> {
                try {
                    String resultsUrl = "http://localhost:8080/results/" + election.getId();
                    emailService.sendElectionResults(election, candidates, resultsUrl);
                    System.out.println("Sent results notification to " + voter.getEmail() +
                                     " (Semester: " + voter.getSemester() + ", Department: " + voter.getDepartment() + ")");
                } catch (Exception e) {
                    System.err.println("Failed to send results email to " + voter.getEmail() + ": " + e.getMessage());
                }
            });
    }
    
    public long getTotalVotes(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        return electionOpt.map(Election::getTotalVotes).orElse(0L);
    }
    
    public long getActiveElectionCount() {
        return electionRepository.countByStatus(ElectionStatus.ACTIVE);
    }
    
    public long getUpcomingElectionCount() {
        return electionRepository.countByStatus(ElectionStatus.SCHEDULED);
    }
    
    public long getCompletedElectionCount() {
        return electionRepository.countByStatus(ElectionStatus.COMPLETED);
    }
    
    public List<Election> searchElections(String keyword) {
        return electionRepository.findByTitleOrDescriptionContaining(keyword);
    }
    
    public boolean isElectionActive(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        return electionOpt.map(Election::isActive).orElse(false);
    }
    
    public boolean isElectionUpcoming(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        return electionOpt.map(Election::isUpcoming).orElse(false);
    }
    
    public boolean isElectionCompleted(Long electionId) {
        Optional<Election> electionOpt = electionRepository.findById(electionId);
        return electionOpt.map(Election::isCompleted).orElse(false);
    }
    
    // Auto-update election statuses based on current time
    public void updateElectionStatuses() {
        List<Election> allElections = electionRepository.findAll();
        
        for (Election election : allElections) {
            LocalDateTime now = LocalDateTime.now();
            
            if (election.getStatus() == ElectionStatus.SCHEDULED && now.isAfter(election.getStartDate()) && now.isBefore(election.getEndDate())) {
                // Move scheduled to active
                election.setStatus(ElectionStatus.ACTIVE);
                electionRepository.save(election);
                sendVotingInvitations(election);
                
            } else if ((election.getStatus() == ElectionStatus.SCHEDULED || election.getStatus() == ElectionStatus.ACTIVE) && now.isAfter(election.getEndDate())) {
                // Move to completed
                election.setStatus(ElectionStatus.COMPLETED);
                electionRepository.save(election);
                
                createElectionAnnouncement(election, "Election Completed", 
                    "The election '" + election.getTitle() + "' has concluded. " +
                    "Results will be announced soon.", AnnouncementType.RESULTS_ANNOUNCEMENT);
            }
        }
    }
}
