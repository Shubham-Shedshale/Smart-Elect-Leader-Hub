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
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )

            .authorizeHttpRequests(authz -> authz

                // Public pages and static resources
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/otp/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/admin/login"
                ).permitAll()

                // Public authentication endpoints
                .requestMatchers(
                    "/api/health",
                    "/api/auth/login",
                    "/api/auth/admin-login",
                    "/api/auth/register-voter",
                    "/api/auth/verify-otp",
                    "/api/auth/resend-otp"
                ).permitAll()

                // Admin creation must not be publicly accessible
                .requestMatchers("/api/auth/create-admin")
                    .hasRole("ADMIN")

                // Admin pages and APIs require the ADMIN role
                .requestMatchers("/admin/**", "/api/admin/**")
                    .hasRole("ADMIN")

                // Session endpoints should require authentication
                .requestMatchers("/api/session/**")
                    .authenticated()

                // All remaining APIs require authentication
                .requestMatchers("/api/**")
                    .authenticated()

                // Everything else requires authentication
                .anyRequest()
                    .authenticated()
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

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response,
                                           authException) -> {

                    String acceptHeader =
                            request.getHeader("Accept");

                    String xRequestedWith =
                            request.getHeader("X-Requested-With");

                    if ("XMLHttpRequest".equals(xRequestedWith)
                            || (acceptHeader != null
                            && acceptHeader.contains("application/json"))) {

                        response.setStatus(
                                HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");

                        response.getWriter().write(
                            "{\"error\":\"SESSION_EXPIRED\","
                            + "\"message\":\"Session expired\","
                            + "\"redirect\":\"/login\"}"
                        );

                    } else {
                        response.sendRedirect("/login?expired=true");
                    }
                })
            );

        return http.build();
    }
}