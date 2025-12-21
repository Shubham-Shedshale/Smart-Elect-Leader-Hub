package com.evoting.controller;

import com.evoting.model.User;
import com.evoting.model.UserRole;
import com.evoting.service.UserService;
import com.evoting.security.CustomUserDetailsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import com.evoting.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // Simple authentication for voters
            Optional<User> authenticatedUser = userService.authenticateUser(request.getEmail(), request.getPassword());

            if (authenticatedUser.isPresent()) {
                User user = authenticatedUser.get();

                // Create authentication token for session
                CustomUserDetailsService.UserPrincipal userPrincipal =
                    CustomUserDetailsService.UserPrincipal.create(user);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Create session if it doesn't exist
                HttpSession session = httpRequest.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("email", user.getEmail());
                userInfo.put("role", user.getRole().name());
                userInfo.put("firstName", user.getFirstName());
                userInfo.put("lastName", user.getLastName());
                userInfo.put("fullName", user.getFullName());

                response.put("user", userInfo);

                return ResponseEntity.ok(response);
            } else {
                // Check if user exists but needs verification or password setup
                Optional<User> userOpt = userService.findByEmail(request.getEmail());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (!user.getIsVerified()) {
                        return ResponseEntity.badRequest().body(Map.of(
                            "error", "Account not verified",
                            "needsVerification", true,
                            "message", "Please verify your account using the OTP sent to your email"
                        ));
                    } else if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                            "error", "Password not set",
                            "needsPasswordSetup", true,
                            "message", "Please set your password to continue"
                        ));
                    }
                }

                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid credentials",
                    "message", "Email or password is incorrect"
                ));
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Login failed",
                "message", "An unexpected error occurred"
            ));
        }
    }
    
    @PostMapping("/register-voter")
    public ResponseEntity<?> registerVoter(@Valid @RequestBody RegisterVoterRequest request) {
        try {
            User user = userService.createVoterWithOTP(request.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Voter registered successfully. OTP sent to email.");
            response.put("userId", user.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Registration failed",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody VerifyOTPRequest request) {
        try {
            boolean verified = userService.verifyOTP(request.getEmail(), request.getOtpCode());

            if (verified) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "OTP verified successfully. Please set your password.");
                response.put("needsPasswordSetup", true);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "OTP verification failed",
                    "message", "Invalid or expired OTP code"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "OTP verification failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@Valid @RequestBody SetPasswordRequest request) {
        try {
            userService.setUserPassword(request.getEmail(), request.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password set successfully. You can now login.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Password setup failed",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@Valid @RequestBody EmailRequest request) {
        try {
            userService.resendOTP(request.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "OTP sent successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to resend OTP",
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Set authentication in security context (creates session)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Create session if it doesn't exist
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            
            // Get authenticated user
            CustomUserDetailsService.UserPrincipal userPrincipal = (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            
            // Check if user is admin
            if (userPrincipal.getRole() != UserRole.ADMIN && userPrincipal.getRole() != UserRole.SUPER_ADMIN) {
                // Clear authentication if not admin
                SecurityContextHolder.clearContext();
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Access denied",
                    "message", "This account is not authorized for admin access"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin login successful");
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userPrincipal.getId());
            userInfo.put("email", userPrincipal.getEmail());
            userInfo.put("role", userPrincipal.getRole().name());
            userInfo.put("firstName", null); // Will be loaded if needed
            userInfo.put("lastName", null);  // Will be loaded if needed
            userInfo.put("fullName", userPrincipal.getEmail()); // Fallback to email
            userInfo.put("isAdmin", true);
            
            response.put("user", userInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Clear any partial authentication
            SecurityContextHolder.clearContext();
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Login failed",
                "message", e.getMessage() != null ? e.getMessage() : "An unexpected error occurred"
            ));
        }
    }
    
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        try {
            User user = userService.createUser(
                request.getEmail(), 
                request.getPassword(), 
                UserRole.ADMIN
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Admin account created successfully");
            response.put("admin", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole().name()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Admin creation failed",
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/debug/users")
    public ResponseEntity<?> debugUsers() {
        try {
            List<User> allUsers = userRepository.findAll();
            List<Map<String, Object>> userInfo = new ArrayList<>();

            for (User user : allUsers) {
                Map<String, Object> info = new HashMap<>();
                info.put("id", user.getId());
                info.put("email", user.getEmail());
                info.put("role", user.getRole());
                info.put("isActive", user.getIsActive());
                info.put("isVerified", user.getIsVerified());
                info.put("hasPassword", user.getPasswordHash() != null && !user.getPasswordHash().trim().isEmpty());
                info.put("firstName", user.getFirstName());
                info.put("lastName", user.getLastName());
                userInfo.add(info);
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "users", userInfo,
                "totalUsers", userInfo.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Debug failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/debug/reset-password")
    public ResponseEntity<?> resetPasswordDebug(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("password");

            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email and password required"
                ));
            }

            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent()) {
                userService.setUserPassword(email, newPassword);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Password reset successfully for " + email
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "User not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Password reset failed",
                "message", e.getMessage()
            ));
        }
    }

    // Request DTOs
    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class RegisterVoterRequest {
        @NotBlank @Email
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class VerifyOTPRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String otpCode;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtpCode() { return otpCode; }
        public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    }
    
    public static class EmailRequest {
        @NotBlank @Email
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
    
    public static class CreateAdminRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class SetPasswordRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}