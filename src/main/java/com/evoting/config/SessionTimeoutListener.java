package com.evoting.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Session Listener to handle session lifecycle events
 * This will log session creation and expiration events
 */
@Component
public class SessionTimeoutListener implements HttpSessionListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionTimeoutListener.class);
    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.debug("Session created: {}", se.getSession().getId());
        logger.info("New session created with ID: {}", se.getSession().getId());
        
        // Set session timeout (30 minutes = 1800 seconds)
        se.getSession().setMaxInactiveInterval(1800);
        logger.debug("Session timeout set to 1800 seconds (30 minutes) for session: {}", se.getSession().getId());
    }
    
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        logger.info("Session destroyed: {}", sessionId);
        
        // Log why the session was destroyed
        if (se.getSession().getLastAccessedTime() > 0) {
            long idleTime = System.currentTimeMillis() - se.getSession().getLastAccessedTime();
            long idleMinutes = idleTime / (60 * 1000);
            logger.info("Session {} was destroyed after {} minutes of inactivity", sessionId, idleMinutes);
        }
        
        // Clear security context when session is destroyed
        try {
            SecurityContextHolder.clearContext();
            logger.debug("Security context cleared for destroyed session: {}", sessionId);
        } catch (Exception e) {
            logger.error("Error clearing security context for session: {}", sessionId, e);
        }
    }
}