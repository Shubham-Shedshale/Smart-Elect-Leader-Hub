package com.evoting.service;

import com.evoting.model.Announcement;
import com.evoting.model.AnnouncementType;
import com.evoting.model.AnnouncementPriority;
import com.evoting.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnnouncementService {
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;
    
    public Announcement createAnnouncement(Long electionId, String title, String content, 
                                         AnnouncementType type, Long createdBy) {
        Announcement announcement = new Announcement(electionId, title, content, type);
        announcement.setCreatedBy(createdBy);
        announcement.setIsPublished(false);
        
        return announcementRepository.save(announcement);
    }
    
    public Optional<Announcement> getAnnouncementById(Long id) {
        return announcementRepository.findById(id);
    }
    
    public List<Announcement> getAnnouncementsByElectionId(Long electionId) {
        return announcementRepository.findByElectionId(electionId);
    }
    
    public List<Announcement> getPublishedAnnouncementsByElectionId(Long electionId) {
        return announcementRepository.findPublishedAnnouncementsByElectionId(electionId);
    }
    
    public List<Announcement> getActiveAnnouncementsByElectionId(Long electionId) {
        return announcementRepository.findActiveAnnouncementsByElectionId(electionId, LocalDateTime.now());
    }
    
    public List<Announcement> getAnnouncementsByType(AnnouncementType type) {
        return announcementRepository.findByType(type);
    }
    
    public List<Announcement> getPublishedAnnouncements() {
        return announcementRepository.findByIsPublishedTrueAndExpiresAtIsNullOrExpiresAtAfter(LocalDateTime.now());
    }
    
    public Announcement updateAnnouncement(Long id, String title, String content, 
                                         AnnouncementType type, AnnouncementPriority priority,
                                         String targetAudience, LocalDateTime expiresAt) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isPresent()) {
            Announcement announcement = announcementOpt.get();
            announcement.setTitle(title);
            announcement.setContent(content);
            announcement.setType(type);
            announcement.setPriority(priority);
            announcement.setTargetAudience(targetAudience);
            announcement.setExpiresAt(expiresAt);
            return announcementRepository.save(announcement);
        }
        throw new RuntimeException("Announcement not found with id: " + id);
    }
    
    public Announcement publishAnnouncement(Long id) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isPresent()) {
            Announcement announcement = announcementOpt.get();
            announcement.publish();
            Announcement savedAnnouncement = announcementRepository.save(announcement);
            
            // Send email notification to target audience
            sendAnnouncementNotification(savedAnnouncement);
            
            return savedAnnouncement;
        }
        throw new RuntimeException("Announcement not found with id: " + id);
    }
    
    public void unpublishAnnouncement(Long id) {
        Optional<Announcement> announcementOpt = announcementRepository.findById(id);
        if (announcementOpt.isPresent()) {
            Announcement announcement = announcementOpt.get();
            announcement.unpublish();
            announcementRepository.save(announcement);
        } else {
            throw new RuntimeException("Announcement not found with id: " + id);
        }
    }
    
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
    
    public long countPublishedAnnouncements(Long electionId) {
        return announcementRepository.countPublishedAnnouncementsByElectionId(electionId);
    }
    
    public List<Announcement> searchAnnouncements(String keyword) {
        return announcementRepository.findByKeywordAndPublished(keyword);
    }
    
    public List<Announcement> getAnnouncementsByAdmin(Long adminId) {
        return announcementRepository.findByCreatedByOrderByCreatedAtDesc(adminId);
    }
    
    public List<Announcement> getUrgentAnnouncements() {
        List<Announcement> allAnnouncements = getPublishedAnnouncements();
        return allAnnouncements.stream()
            .filter(announcement -> announcement.getPriority() == AnnouncementPriority.URGENT)
            .toList();
    }
    
    private void sendAnnouncementNotification(Announcement announcement) {
        String targetAudience = announcement.getTargetAudience();
        if (targetAudience == null || targetAudience.equals("ALL")) {
            // Send to all users
            userService.getActiveUsers().forEach(user -> {
                String announcementUrl = "http://localhost:8080/announcements/" + announcement.getId();
                try {
                    emailService.sendAnnouncementEmail(user, announcement.getTitle(), 
                                                     announcement.getContent(), announcementUrl);
                } catch (Exception e) {
                    System.err.println("Failed to send announcement email to " + user.getEmail() + ": " + e.getMessage());
                }
            });
        } else {
            // Send to specific user group
            // This would be implemented based on your user segmentation logic
        }
    }
    
    // Auto-expire old announcements
    public void expireOldAnnouncements() {
        LocalDateTime now = LocalDateTime.now();
        // This would be implemented with a scheduled task in a real application
    }
    
    public List<Announcement> getRecentAnnouncements(int limit) {
        List<Announcement> allAnnouncements = getPublishedAnnouncements();
        if (allAnnouncements.size() <= limit) {
            return allAnnouncements;
        }
        return allAnnouncements.subList(0, limit);
    }
    
    public boolean hasUnreadAnnouncements(Long userId) {
        // This would be implemented with a user-announcement read tracking system
        // For now, return true if there are any active announcements
        return !getPublishedAnnouncements().isEmpty();
    }
}