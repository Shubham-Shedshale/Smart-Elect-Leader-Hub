package com.evoting.repository;

import com.evoting.model.Election;
import com.evoting.model.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    
    List<Election> findByStatus(ElectionStatus status);
    
    @Query("SELECT e FROM Election e WHERE e.status = :status ORDER BY e.createdAt DESC")
    List<Election> findByStatusOrderByCreatedAtDesc(@Param("status") ElectionStatus status);
    
    @Query("SELECT e FROM Election e WHERE e.startDate > :currentTime ORDER BY e.startDate ASC")
    List<Election> findUpcomingElections(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT e FROM Election e WHERE e.endDate < :currentTime ORDER BY e.endDate DESC")
    List<Election> findCompletedElections(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT e FROM Election e WHERE e.startDate <= :currentTime AND e.endDate >= :currentTime AND e.status = 'ACTIVE'")
    List<Election> findActiveElections(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT e FROM Election e WHERE e.createdBy = :adminId ORDER BY e.createdAt DESC")
    List<Election> findByCreatedByOrderByCreatedAtDesc(@Param("adminId") Long adminId);
    
    @Query("SELECT COUNT(e) FROM Election e WHERE e.status = :status")
    long countByStatus(@Param("status") ElectionStatus status);
    
    @Query("SELECT COUNT(e) FROM Election e WHERE e.startDate >= :startDate AND e.endDate <= :endDate")
    long countElectionsInPeriod(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Election e WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Election> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);
}