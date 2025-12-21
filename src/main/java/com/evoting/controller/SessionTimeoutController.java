package com.evoting.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "*")
public class SessionTimeoutController {

    /**
     * Check if current session is valid
     * This endpoint can be used by frontend to check session status
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkSession(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
            && !authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Session is valid",
                "user", Map.of(
                    "email", authentication.getName(),
                    "authorities", authentication.getAuthorities()
                )
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "valid", false,
                "message", "Session is invalid or expired",
                "redirect", "/login"
            ));
        }
    }

    /**
     * Handle session timeout - called when session expires
     */
    @GetMapping("/timeout")
    public ResponseEntity<?> handleSessionTimeout(HttpServletRequest request) {
        // Invalidate any existing session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        // Clear security context
        SecurityContextHolder.clearContext();
        
        // Check if this is an AJAX request
        String acceptHeader = request.getHeader("Accept");
        String xRequestedWith = request.getHeader("X-Requested-With");
        
        if ("XMLHttpRequest".equals(xRequestedWith) || 
            (acceptHeader != null && acceptHeader.contains("application/json"))) {
            // Return JSON response for AJAX requests
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "SESSION_EXPIRED",
                "message", "Your session has expired. Please log in again.",
                "redirect", "/login",
                "timestamp", System.currentTimeMillis()
            ));
        } else {
            // Return HTML response for regular browser requests
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Session expired");
            response.put("redirect", "/login?expired=true");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Manually logout user and invalidate session
     */
    @PostMapping("/logout")
    public ResponseEntity<?> manualLogout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, null, authentication);
        }
        
        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Logged out successfully",
            "redirect", "/login?logout=true"
        ));
    }

    /**
     * Get session information
     */
    @GetMapping("/info")
    public ResponseEntity<?> getSessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        
        Map<String, Object> sessionInfo = new HashMap<>();
        sessionInfo.put("sessionId", session.getId());
        sessionInfo.put("creationTime", session.getCreationTime());
        sessionInfo.put("lastAccessedTime", session.getLastAccessedTime());
        sessionInfo.put("maxInactiveInterval", session.getMaxInactiveInterval());
        
        // Calculate remaining time in seconds
        long now = System.currentTimeMillis();
        long lastAccessed = session.getLastAccessedTime();
        int maxInactive = session.getMaxInactiveInterval() * 1000; // Convert to milliseconds
        long remainingTime = (lastAccessed + maxInactive) - now;
        
        sessionInfo.put("remainingTimeSeconds", Math.max(0, remainingTime / 1000));
        sessionInfo.put("isExpired", remainingTime <= 0);
        
        // Get current user info
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            sessionInfo.put("authenticated", true);
            sessionInfo.put("username", authentication.getName());
            sessionInfo.put("authorities", authentication.getAuthorities());
        } else {
            sessionInfo.put("authenticated", false);
        }
        
        return ResponseEntity.ok(sessionInfo);
    }
}
