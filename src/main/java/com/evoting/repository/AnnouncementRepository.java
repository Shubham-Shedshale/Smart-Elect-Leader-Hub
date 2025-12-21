package com.evoting.repository;

import com.evoting.model.Announcement;
import com.evoting.model.AnnouncementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    List<Announcement> findByElectionId(Long electionId);
    
    List<Announcement> findByElectionIdAndIsPublishedTrue(Long electionId);
    
    List<Announcement> findByIsPublishedTrueAndExpiresAtIsNullOrExpiresAtAfter(LocalDateTime currentTime);
    
    List<Announcement> findByType(AnnouncementType type);
    
    @Query("SELECT a FROM Announcement a WHERE a.electionId = :electionId AND a.isPublished = true ORDER BY a.publishedAt DESC")
    List<Announcement> findPublishedAnnouncementsByElectionId(@Param("electionId") Long electionId);
    
    @Query("SELECT a FROM Announcement a WHERE a.createdBy = :adminId ORDER BY a.createdAt DESC")
    List<Announcement> findByCreatedByOrderByCreatedAtDesc(@Param("adminId") Long adminId);
    
    @Query("SELECT a FROM Announcement a WHERE a.electionId = :electionId AND a.isPublished = true AND (a.expiresAt IS NULL OR a.expiresAt > :currentTime) ORDER BY a.priority DESC, a.publishedAt DESC")
    List<Announcement> findActiveAnnouncementsByElectionId(@Param("electionId") Long electionId, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT a FROM Announcement a WHERE a.targetAudience = :audience AND a.isPublished = true AND (a.expiresAt IS NULL OR a.expiresAt > :currentTime) ORDER BY a.priority DESC, a.publishedAt DESC")
    List<Announcement> findByTargetAudienceAndActive(@Param("audience") String audience, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT a FROM Announcement a WHERE a.electionId = :electionId AND a.type = :type AND a.isPublished = true ORDER BY a.publishedAt DESC")
    List<Announcement> findByElectionIdAndTypeOrderByPublishedAtDesc(@Param("electionId") Long electionId, @Param("type") AnnouncementType type);
    
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.electionId = :electionId AND a.isPublished = true")
    long countPublishedAnnouncementsByElectionId(@Param("electionId") Long electionId);
    
    @Query("SELECT a FROM Announcement a WHERE (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) AND a.isPublished = true ORDER BY a.publishedAt DESC")
    List<Announcement> findByKeywordAndPublished(@Param("keyword") String keyword);
}