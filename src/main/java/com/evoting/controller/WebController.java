package com.evoting.controller;

import com.evoting.model.Election;
import com.evoting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private ElectionService electionService;
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "login";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
    
    @GetMapping("/voter/dashboard")
    public String voterDashboard() {
        return "voter-dashboard";
    }
    
    // Additional voter routes
    @GetMapping("/voter/elections")
    public String voterElections() {
        return "voter-elections"; // This would be a separate template
    }
    
    @GetMapping("/voter/history")
    public String voterHistory() {
        return "voter-history"; // This would be a separate template
    }
    
    @GetMapping("/voter/announcements")
    public String voterAnnouncements() {
        return "voter-announcements"; // This would be a separate template
    }

    @GetMapping("/voter/elections/{electionId}/vote")
    public String voterVote(@PathVariable Long electionId, Model model) {
        Optional<Election> electionOpt = electionService.getElectionById(electionId);
        if (electionOpt.isPresent()) {
            model.addAttribute("election", electionOpt.get());
            model.addAttribute("electionId", electionId);
            return "voter-vote"; // Voting page template
        } else {
            // Election not found, redirect to elections page
            return "redirect:/voter/elections";
        }
    }
}