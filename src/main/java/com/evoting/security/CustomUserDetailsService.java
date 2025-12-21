package com.evoting.security;

import com.evoting.model.User;
import com.evoting.model.UserRole;
import com.evoting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User is not active: " + email);
        }
        
        return UserPrincipal.create(user);
    }
    
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        
        return UserPrincipal.create(user);
    }
    
    public static class UserPrincipal implements UserDetails {
        private Long id;
        private String email;
        private String password;
        private UserRole role;
        private Boolean isVerified;
        private Boolean isActive;
        private List<SimpleGrantedAuthority> authorities;
        
        public UserPrincipal(Long id, String email, String password, UserRole role, 
                           Boolean isVerified, Boolean isActive, List<SimpleGrantedAuthority> authorities) {
            this.id = id;
            this.email = email;
            this.password = password;
            this.role = role;
            this.isVerified = isVerified;
            this.isActive = isActive;
            this.authorities = authorities;
        }
        
        public static UserPrincipal create(User user) {
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
            
            return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getIsVerified(),
                user.getIsActive(),
                authorities
            );
        }
        
        public Long getId() {
            return id;
        }
        
        public String getEmail() {
            return email;
        }
        
        public UserRole getRole() {
            return role;
        }
        
        @Override
        public String getUsername() {
            return email;
        }
        
        @Override
        public String getPassword() {
            return password;
        }
        
        @Override
        public List<SimpleGrantedAuthority> getAuthorities() {
            return authorities;
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return this.isActive && this.isVerified;
        }
        
        // Custom validation for OTP verification
        public boolean isVerifiedForOTP() {
            return this.isVerified;
        }
    }
}