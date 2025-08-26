package com.srilanka.realestate.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Test endpoint - create a sample user
    @GetMapping("/create-test-user")
    public String createTestUser() {
        // Create a new user
        User user = new User();
        user.setFirstName("Kamal");
        user.setLastName("Silva");
        user.setEmail("kamal@gmail.com");
        user.setPassword("password123");
        user.setPhone("0771234567");
        user.setRole("BUYER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save to database
        userRepository.save(user);

        return "Test user created successfully! Check your database.";
    }

    // Get all users
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get users by role
    @GetMapping("/buyers")
    public List<User> getBuyers() {
        return userRepository.findByRole("BUYER");
    }

    // Count total users
    @GetMapping("/count")
    public String getUserCount() {
        long count = userRepository.count();
        return "Total users in database: " + count;
    }

    // Get current authenticated user's profile
    @GetMapping("/profile")
    public User getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("No authenticated user");
        }
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
