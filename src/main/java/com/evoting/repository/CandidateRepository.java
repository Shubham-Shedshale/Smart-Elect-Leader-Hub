package com.evoting.repository;

import com.evoting.model.Candidate;
import com.evoting.model.CandidateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    List<Candidate> findByElectionId(Long electionId);
    
    List<Candidate> findByElectionIdAndStatus(Long electionId, CandidateStatus status);
    
    Optional<Candidate> findByIdAndElectionId(Long id, Long electionId);
    
    @Query("SELECT c FROM Candidate c WHERE c.electionId = :electionId ORDER BY c.votesCount DESC")
    List<Candidate> findByElectionIdOrderByVotesCountDesc(@Param("electionId") Long electionId);
    
    @Query("SELECT c FROM Candidate c WHERE c.electionId = :electionId AND c.status = :status ORDER BY c.votesCount DESC")
    List<Candidate> findByElectionIdAndStatusOrderByVotesCountDesc(@Param("electionId") Long electionId, @Param("status") CandidateStatus status);
    
    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.electionId = :electionId AND c.status = :status")
    long countByElectionIdAndStatus(@Param("electionId") Long electionId, @Param("status") CandidateStatus status);
    
    @Query("SELECT c FROM Candidate c WHERE c.electionId = :electionId AND (c.fullName LIKE %:keyword% OR c.partyName LIKE %:keyword% OR c.position LIKE %:keyword%)")
    List<Candidate> findByElectionIdAndKeyword(@Param("electionId") Long electionId, @Param("keyword") String keyword);
    
    @Query("SELECT c FROM Candidate c WHERE c.createdBy = :adminId ORDER BY c.createdAt DESC")
    List<Candidate> findByCreatedByOrderByCreatedAtDesc(@Param("adminId") Long adminId);
    
    boolean existsByElectionIdAndEmailAddress(Long electionId, String email);
    
    boolean existsByElectionIdAndFullName(Long electionId, String fullName);
    
    @Query("SELECT c FROM Candidate c WHERE c.electionId = :electionId ORDER BY c.createdAt DESC")
    List<Candidate> findByElectionIdOrderByCreatedAtDesc(@Param("electionId") Long electionId);
}