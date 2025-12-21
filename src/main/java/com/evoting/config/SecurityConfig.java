package com.evoting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.evoting.security.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
// Disabled method security for simplified school project authentication
// @EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Temporarily disable CORS to fix startup issue
            // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - these must come first
                .requestMatchers("/login", "/register", "/otp/**", "/css/**", "/js/**", "/images/**", "/actuator/**").permitAll()
                .requestMatchers("/admin/login").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/auth/create-admin").permitAll()
                .requestMatchers("/api/auth/admin-login").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register-voter").permitAll()
                .requestMatchers("/api/auth/verify-otp").permitAll()
                .requestMatchers("/api/auth/resend-otp").permitAll()
                
                // Session management endpoints
                .requestMatchers("/api/session/**").permitAll()
                
                // Admin endpoints (temporarily disabled for testing)
                .requestMatchers("/admin/dashboard").permitAll()
                .requestMatchers("/api/admin/dashboard/stats").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                .requestMatchers("/admin/**").permitAll()
                
                // All other API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            // Add exception handling for session timeout
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    // Check if it's an AJAX request
                    String acceptHeader = request.getHeader("Accept");
                    String xRequestedWith = request.getHeader("X-Requested-With");
                    
                    if ("XMLHttpRequest".equals(xRequestedWith) || 
                        (acceptHeader != null && acceptHeader.contains("application/json"))) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"SESSION_EXPIRED\",\"message\":\"Session expired\",\"redirect\":\"/login\"}");
                    } else {
                        response.sendRedirect("/login?expired=true");
                    }
                })
            );
        
        return http.build();
    }
    
    // @Bean
    // public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
    //     org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
    //     configuration.setAllowedOriginPatterns(java.util.List.of("http://localhost:*", "http://127.0.0.1:*"));
    //     configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    //     configuration.setAllowedHeaders(java.util.List.of("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin"));
    //     configuration.setAllowCredentials(true);
    //     
    //     org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
    //     source.registerCorsConfiguration("/**", configuration);
    //     return source;
    // }
}
