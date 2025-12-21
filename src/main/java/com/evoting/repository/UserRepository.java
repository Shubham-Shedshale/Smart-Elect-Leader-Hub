package com.evoting.repository;

import com.evoting.model.User;
import com.evoting.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsVerifiedTrue();
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    @Query("SELECT u FROM User u WHERE u.otpCode = :otpCode AND u.otpExpiresAt > :currentTime")
    Optional<User> findByOtpCodeAndNotExpired(@Param("otpCode") String otpCode, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT u FROM User u WHERE u.lastLogin IS NOT NULL ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true ORDER BY u.createdAt DESC")
    List<User> findActiveUsersByRole(@Param("role") UserRole role);
    
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
    List<User> findUsersByDomain(@Param("domain") String domain);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countActiveUsersByRole(@Param("role") UserRole role);
}