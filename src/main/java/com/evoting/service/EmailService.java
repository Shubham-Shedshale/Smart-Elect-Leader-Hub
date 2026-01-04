package com.evoting.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.evoting.model.Candidate;
import com.evoting.model.Election;
import com.evoting.model.User;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    public void sendOTPCode(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Your Voting System OTP Code");
        
        String text = String.format(
            "Hello,\n\n" +
            "Your One-Time Password (OTP) for the Electronic Voting System is: %s\n\n" +
            "This code will expire in 15 minutes.\n\n" +
            "If you didn't request this code, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Electronic Voting System Team",
            user.getOtpCode()
        );
        
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Electronic Voting System");

        String text = String.format(
            "Welcome %s!\n\n" +
            "Your account has been created successfully in the Electronic Voting System.\n\n" +
            "You can now log in and participate in elections when they are available.\n\n" +
            "Login URL: %s/login\n\n" +
            "Thank you for joining us!\n\n" +
            "Best regards,\n" +
            "Electronic Voting System Team",
            user.getEmail(),
            baseUrl
        );

        message.setText(text);
        mailSender.send(message);
    }

    public void sendVoterCredentialsEmail(User user, String plainPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Your Voter Account Credentials - Electronic Voting System");

        String semesterInfo = user.getSemester() != null ? user.getSemester().getDisplayName() : "Not specified";
        String departmentInfo = user.getDepartment() != null ? user.getDepartment() : "Not specified";

        String text = String.format(
            "Hello,\n\n" +
            "Your voter account has been created in the Electronic Voting System.\n\n" +
            "Here are your login credentials:\n\n" +
            "Username: %s\n" +
            "Email: %s\n" +
            "Password: %s\n" +
            "Semester: %s\n" +
            "Department: %s\n\n" +
            "Please log in using these credentials:\n" +
            "Login URL: %s/login\n\n" +
            "Important:\n" +
            "- Keep this information secure\n" +
            "- Change your password after first login if desired\n" +
            "- You will receive notifications when elections for your semester and department become available\n\n" +
            "Thank you for participating in our democratic process!\n\n" +
            "Best regards,\n" +
            "Electronic Voting System Team",
            user.getEmail(),
            user.getEmail(),
            plainPassword,
            semesterInfo,
            departmentInfo,
            baseUrl
        );

        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendVotingInvitation(User user, Election election, String votingUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("You are invited to vote in: " + election.getTitle());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        
        String text = String.format(
            "Hello %s,\n\n" +
            "You are invited to participate in the upcoming election:\n\n" +
            "Election: %s\n" +
            "Description: %s\n" +
            "Start Date: %s\n" +
            "End Date: %s\n\n" +
            "Click the link below to vote:\n%s\n\n" +
            "Please vote before the deadline.\n\n" +
            "Best regards,\n" +
            "Electronic Voting System Team",
            user.getEmail(),
            election.getTitle(),
            election.getDescription() != null ? election.getDescription() : "No description",
            election.getStartDate().format(formatter),
            election.getEndDate().format(formatter),
            votingUrl
        );
        
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendElectionResults(Election election, List<Candidate> candidates, String resultsUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setSubject("Election Results: " + election.getTitle());
        
        // Get all voter emails (for demo, sending to all voters)
        // In real implementation, you'd have a proper mailing list
        
        StringBuilder text = new StringBuilder(
            "Dear Voter,\n\n" +
            "The election has concluded. Here are the results:\n\n" +
            "Election: " + election.getTitle() + "\n" +
            "Total Votes: " + election.getTotalVotes() + "\n\n" +
            "Results:\n"
        );
        
        for (int i = 0; i < candidates.size(); i++) {
            Candidate candidate = candidates.get(i);
            text.append(String.format("%d. %s - %d votes\n", i + 1, candidate.getFullName(), candidate.getVotesCount()));
        }
        
        text.append("\nView detailed results: ").append(resultsUrl);
        text.append("\n\nThank you for participating!\n\nBest regards,\nElectronic Voting System Team");
        
        message.setText(text.toString());
        mailSender.send(message);
    }
    
    public void sendBulkEmails(List<String> emails, String subject, String content) {
        for (String email : emails) {
            try {
                sendSingleEmail(email, subject, content);
            } catch (Exception e) {
                System.err.println("Failed to send email to " + email + ": " + e.getMessage());
            }
        }
    }
    
    public void sendSingleEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
    
    public void sendAnnouncementEmail(User user, String title, String content, String announcementUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Announcement: " + title);
        
        String text = String.format(
            "Hello %s,\n\n" +
            "New announcement from the Electronic Voting System:\n\n" +
            "Title: %s\n\n" +
            "Content: %s\n\n" +
            "Read more: %s\n\n" +
            "Best regards,\n" +
            "Electronic Voting System Team",
            user.getEmail(),
            title,
            content,
            announcementUrl
        );
        
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(User user, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Password Reset Request");
        
        String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
        
        String text = String.format(
            "Hello,\n\n" +
            "You have requested a password reset for your Electronic Voting System account.\n\n" +
            "Click the link below to reset your password:\n%s\n\n" +
            "This link will expire in 24 hours.\n\n" +
            "If you didn't request this reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "Electronic Voting System Team",
            resetUrl
        );
        
        message.setText(text);
        mailSender.send(message);
    }
}
