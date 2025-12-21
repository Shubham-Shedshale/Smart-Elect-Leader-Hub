/**
 * Session Timeout Manager
 * Automatically handles session expiration and logs out users
 */

class SessionTimeoutManager {
    constructor(options = {}) {
        this.checkInterval = options.checkInterval || 60000; // Check every minute
        this.warningTime = options.warningTime || 120000; // Show warning 2 minutes before expiry
        this.logoutUrl = options.logoutUrl || '/login';
        this.apiUrl = options.apiUrl || '/api/session/check';
        this.timeoutUrl = options.timeoutUrl || '/api/session/timeout';
        
        this.sessionTimer = null;
        this.warningTimer = null;
        this.hasShownWarning = false;
        
        this.init();
    }
    
    init() {
        // Start monitoring session
        this.startSessionMonitoring();
        
        // Handle browser visibility changes (tab switch, etc.)
        document.addEventListener('visibilitychange', () => {
            if (!document.hidden) {
                this.checkSession();
            }
        });
        
        // Handle beforeunload to warn about unsaved changes
        window.addEventListener('beforeunload', (e) => {
            if (this.hasUnsavedChanges()) {
                e.preventDefault();
                e.returnValue = '';
            }
        });
    }
    
    startSessionMonitoring() {
        // Check session status immediately
        this.checkSession();
        
        // Set up periodic checking
        this.sessionTimer = setInterval(() => {
            this.checkSession();
        }, this.checkInterval);
    }
    
    async checkSession() {
        try {
            const response = await fetch(this.apiUrl, {
                method: 'GET',
                credentials: 'same-origin',
                headers: {
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            
            if (!response.ok) {
                // Session is expired or invalid
                if (response.status === 401) {
                    this.handleSessionExpired();
                    return;
                }
            }
            
            const data = await response.json();
            
            if (data.valid) {
                this.hasShownWarning = false;
                
                // Check if session is about to expire
                if (data.remainingTimeSeconds) {
                    const remainingTime = data.remainingTimeSeconds * 1000;
                    
                    if (remainingTime <= this.warningTime && !this.hasShownWarning) {
                        this.showSessionWarning(remainingTime);
                        this.hasShownWarning = true;
                    }
                }
            } else {
                this.handleSessionExpired();
            }
            
        } catch (error) {
            console.error('Session check failed:', error);
            // If we can't check the session, assume it's still valid
            // Don't automatically log out on network errors
        }
    }
    
    async handleSessionExpired() {
        console.log('Session expired - logging out user');
        
        // Clear any existing timers
        this.clearTimers();
        
        // Show session expired message
        this.showSessionExpiredDialog();
        
        // Logout the user
        try {
            await fetch('/api/session/logout', {
                method: 'POST',
                credentials: 'same-origin'
            });
        } catch (error) {
            console.error('Logout request failed:', error);
        }
        
        // Redirect to login page after a delay
        setTimeout(() => {
            window.location.href = `${this.logoutUrl}?expired=true`;
        }, 3000);
    }
    
    showSessionWarning(remainingTime) {
        const minutes = Math.floor(remainingTime / 60000);
        const seconds = Math.floor((remainingTime % 60000) / 1000);
        
        const message = `Your session will expire in ${minutes}:${seconds.toString().padStart(2, '0')}. ` +
                       `Click "Extend Session" to stay logged in.`;
        
        if (confirm(message)) {
            // User wants to extend session - could make an API call to refresh session
            this.extendSession();
        } else {
            // User acknowledged the warning but chose not to extend
            this.hasShownWarning = false;
        }
    }
    
    showSessionExpiredDialog() {
        alert('Your session has expired. You will be redirected to the login page.');
    }
    
    async extendSession() {
        try {
            const response = await fetch('/api/session/refresh', {
                method: 'POST',
                credentials: 'same-origin'
            });
            
            if (response.ok) {
                console.log('Session extended successfully');
                this.hasShownWarning = false;
            }
        } catch (error) {
            console.error('Failed to extend session:', error);
        }
    }
    
    clearTimers() {
        if (this.sessionTimer) {
            clearInterval(this.sessionTimer);
            this.sessionTimer = null;
        }
        
        if (this.warningTimer) {
            clearTimeout(this.warningTimer);
            this.warningTimer = null;
        }
    }
    
    hasUnsavedChanges() {
        // Check if there are any unsaved changes in forms
        const forms = document.querySelectorAll('form');
        for (let form of forms) {
            if (form.dataset.dirty === 'true') {
                return true;
            }
        }
        return false;
    }
    
    destroy() {
        this.clearTimers();
        // Remove event listeners if needed
    }
}

// Initialize session timeout manager when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Only initialize if user is logged in (check for admin or user elements)
    if (document.querySelector('.admin-dashboard, .user-dashboard, .navbar-nav')) {
        window.sessionTimeoutManager = new SessionTimeoutManager({
            checkInterval: 60000, // Check every minute
            warningTime: 120000,  // Warn 2 minutes before expiry
            logoutUrl: '/login',
            apiUrl: '/api/session/check',
            timeoutUrl: '/api/session/timeout'
        });
        
        console.log('Session timeout manager initialized');
    }
});

// Utility function to mark forms as dirty
function markFormDirty(formElement, isDirty) {
    if (formElement) {
        formElement.dataset.dirty = isDirty ? 'true' : 'false';
    }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = SessionTimeoutManager;
}