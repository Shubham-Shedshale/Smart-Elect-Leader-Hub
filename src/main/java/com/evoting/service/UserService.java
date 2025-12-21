package com.evoting.service;

import com.evoting.model.User;
import com.evoting.model.UserRole;
import com.evoting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    public User createUser(String email, String password, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        user.setIsVerified(role == UserRole.VOTER || role == UserRole.ADMIN); // Voters and Admins are auto-verified for demo
        
        User savedUser = userRepository.save(user);
        
        // Send welcome email to voters
        if (role == UserRole.VOTER) {
            try {
                emailService.sendWelcomeEmail(savedUser);
            } catch (Exception e) {
                // Log error but don't fail user creation
                System.err.println("Failed to send welcome email: " + e.getMessage());
            }
        }
        
        return savedUser;
    }
    
    public User createVoterWithOTP(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with email " + email + " already exists");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setRole(UserRole.VOTER);
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setOtpCode(generateOTP());
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(15));
        
        User savedUser = userRepository.save(user);
        
        // Send OTP email
        try {
            emailService.sendOTPCode(savedUser);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
        
        return savedUser;
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public boolean verifyOTP(String email, String otp) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        if (user.getOtpCode() == null || 
            !user.getOtpCode().equals(otp) || 
            user.isOtpExpired()) {
            return false;
        }
        
        // OTP is valid, verify the user
        user.setIsVerified(true);
        user.setOtpCode(null);
        user.setOtpExpiresAt(null);
        userRepository.save(user);
        
        return true;
    }
    
    public String generateOTP() {
        return String.format("%06d", (int) (Math.random() * 900000));
    }
    
    public void updateLastLogin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    public List<User> getVoters() {
        return userRepository.findByRole(UserRole.VOTER);
    }
    
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }
    
    public long countActiveVoters() {
        return userRepository.countActiveUsersByRole(UserRole.VOTER);
    }
    
    public List<User> getRecentlyActiveUsers() {
        return userRepository.findRecentlyActiveUsers();
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public void deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            userRepository.save(user);
        }
    }
    
    public void activateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            userRepository.save(user);
        }
    }
    
    public void updateUserProfile(Long userId, String firstName, String lastName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            userRepository.save(user);
        }
    }
    
    public void resendOTP(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setOtpCode(generateOTP());
            user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);
            
            try {
                emailService.sendOTPCode(user);
            } catch (Exception e) {
                System.err.println("Failed to resend OTP email: " + e.getMessage());
            }
        }
    }
    
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check if user is active and verified
            if (user.getIsActive() && user.getIsVerified()) {
                // Check if password hash exists and is not empty
                if (user.getPasswordHash() != null && !user.getPasswordHash().trim().isEmpty()) {
                    try {
                        if (passwordEncoder.matches(password, user.getPasswordHash())) {
                            updateLastLogin(user.getId());
                            return userOpt;
                        }
                    } catch (Exception e) {
                        // Password verification failed
                        return Optional.empty();
                    }
                } else {
                    // User doesn't have a password set
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    public void setUserPassword(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPasswordHash(passwordEncoder.encode(password));
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}