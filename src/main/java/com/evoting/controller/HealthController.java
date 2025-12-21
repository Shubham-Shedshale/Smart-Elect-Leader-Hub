package com.evoting.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller to verify Spring Boot is working
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Electronic Voting System is running!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/api")
    public String welcome() {
        return "Welcome to Electronic Voting System! Spring Boot is working correctly.";
    }
}
