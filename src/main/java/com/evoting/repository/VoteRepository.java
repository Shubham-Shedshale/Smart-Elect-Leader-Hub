package com.evoting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evoting.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByUserIdAndElectionId(Long userId, Long electionId);
    
    List<Vote> findByElectionId(Long electionId);
    
    List<Vote> findByCandidateId(Long candidateId);
    
    List<Vote> findByUserId(Long userId);
    
    boolean existsByUserIdAndElectionId(Long userId, Long electionId);
    
    @Query("SELECT v FROM Vote v WHERE v.electionId = :electionId ORDER BY v.votedAt DESC")
    List<Vote> findByElectionIdOrderByVotedAtDesc(@Param("electionId") Long electionId);
    
    @Query("SELECT v FROM Vote v WHERE v.candidateId = :candidateId ORDER BY v.votedAt DESC")
    List<Vote> findByCandidateIdOrderByVotedAtDesc(@Param("candidateId") Long candidateId);
    
    @Query("SELECT v FROM Vote v WHERE v.userId = :userId ORDER BY v.votedAt DESC")
    List<Vote> findByUserIdOrderByVotedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.electionId = :electionId")
    long countByElectionId(@Param("electionId") Long electionId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.candidateId = :candidateId")
    long countByCandidateId(@Param("candidateId") Long candidateId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT v.candidateId, COUNT(v) as voteCount FROM Vote v WHERE v.electionId = :electionId GROUP BY v.candidateId ORDER BY voteCount DESC")
    List<Object[]> findVoteCountsByElectionId(@Param("electionId") Long electionId);
    
    @Query("SELECT v.candidateId, COUNT(v) as voteCount FROM Vote v WHERE v.electionId = :electionId AND v.votedAt >= :startDate AND v.votedAt <= :endDate GROUP BY v.candidateId ORDER BY voteCount DESC")
    List<Object[]> findVoteCountsByElectionIdAndDateRange(@Param("electionId") Long electionId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
    
    // @Query("SELECT COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND v.votedAt >= CAST(CURRENT_TIMESTAMP AS DATE)")
    // long countVotesTodayByElectionId(@Param("electionId") Long electionId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND v.votedAt >= :startDate AND v.votedAt < :endDate")
    long countVotesInTimeRange(@Param("electionId") Long electionId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT HOUR(v.votedAt), COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND DATE(v.votedAt) >= DATE(:startDate) AND DATE(v.votedAt) <= DATE(:endDate) GROUP BY HOUR(v.votedAt) ORDER BY HOUR(v.votedAt)")
    List<Object[]> findVoteActivityByHour(@Param("electionId") Long electionId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT DATE(v.votedAt), COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND DATE(v.votedAt) >= DATE(:startDate) AND DATE(v.votedAt) <= DATE(:endDate) GROUP BY DATE(v.votedAt) ORDER BY DATE(v.votedAt)")
    List<Object[]> findVoteTrendsByDay(@Param("electionId") Long electionId, @Param("startDate") java.time.LocalDateTime startDate, @Param("endDate") java.time.LocalDateTime endDate);
}
