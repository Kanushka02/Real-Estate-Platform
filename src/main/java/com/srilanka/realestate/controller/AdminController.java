package com.srilanka.realestate.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.PropertyRepository;
import com.srilanka.realestate.repository.UserRepository;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // All endpoints require ADMIN role
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    // SavedPropertyRepository not available; favorites metrics disabled

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // User statistics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long sellers = userRepository.countByRole("SELLER");
        long buyers = userRepository.countByRole("BUYER");
        long admins = userRepository.countByRole("ADMIN");

        // Property statistics
        long totalProperties = propertyRepository.count();
        long activeProperties = propertyRepository.countByStatus("ACTIVE");
        long propertiesForSale = propertyRepository.countByPropertyType("SALE");
        long propertiesForRent = propertyRepository.countByPropertyType("RENT");

        // Saved properties statistics
        long totalSavedProperties = 0L; // favorites tracking disabled

        stats.put("users", Map.of(
                "total", totalUsers,
                "active", activeUsers,
                "sellers", sellers,
                "buyers", buyers,
                "admins", admins
        ));

        stats.put("properties", Map.of(
                "total", totalProperties,
                "active", activeProperties,
                "forSale", propertiesForSale,
                "forRent", propertiesForRent
        ));

        stats.put("favorites", Map.of(
                "total", totalSavedProperties
        ));

        stats.put("generatedAt", LocalDateTime.now());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get all users with pagination
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userRepository.findByRole(role.toUpperCase());
        return ResponseEntity.ok(users);
    }

    /**
     * Activate/Deactivate user
     */
    @PatchMapping("/users/{userId}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            user.setIsActive(!user.getIsActive());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            String status = user.getIsActive() ? "activated" : "deactivated";

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"User " + status + " successfully\",\n" +
                    "  \"userId\": " + userId + ",\n" +
                    "  \"newStatus\": " + user.getIsActive() + "\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to toggle user status: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Get all properties with seller information
     */
    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getAllPropertiesForAdmin() {
        List<Property> properties = propertyRepository.findAll();
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by status
     */
    @GetMapping("/properties/status/{status}")
    public ResponseEntity<List<Property>> getPropertiesByStatus(@PathVariable String status) {
        List<Property> properties = propertyRepository.findByStatus(status.toUpperCase());
        return ResponseEntity.ok(properties);
    }

    /**
     * Change property status (ACTIVE, INACTIVE, SOLD, etc.)
     */
    @PatchMapping("/properties/{propertyId}/status")
    public ResponseEntity<?> changePropertyStatus(@PathVariable Long propertyId,
                                                  @RequestParam String status) {
        try {
            Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
            if (propertyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Property property = propertyOpt.get();
            property.setStatus(status.toUpperCase());
            property.setUpdatedAt(LocalDateTime.now());
            propertyRepository.save(property);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Property status updated to " + status + "\",\n" +
                    "  \"propertyId\": " + propertyId + ",\n" +
                    "  \"newStatus\": \"" + status.toUpperCase() + "\"\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to update property status: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Delete property (admin override)
     */
    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<?> deletePropertyAsAdmin(@PathVariable Long propertyId) {
        try {
            Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
            if (propertyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            propertyRepository.deleteById(propertyId);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Property deleted successfully by admin\",\n" +
                    "  \"propertyId\": " + propertyId + "\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to delete property: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Get recent activities (last 50 properties and users)
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();

        // Recent properties (last 10)
        List<Property> recentProperties = propertyRepository.findTop10ByOrderByCreatedAtDesc();

        // Recent users (last 10)
        List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();

        activities.put("recentProperties", recentProperties);
        activities.put("recentUsers", recentUsers);
        activities.put("generatedAt", LocalDateTime.now());

        return ResponseEntity.ok(activities);
    }

    /**
     * Search users by email or name
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        List<User> users = userRepository.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                query, query, query);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user details with their properties
     */
    @GetMapping("/users/{userId}/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();
            List<Property> userProperties = propertyRepository.findBySeller(user);
            long savedCount = 0L; // favorites tracking disabled

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("user", user);
            userDetails.put("properties", userProperties);
            userDetails.put("propertiesCount", userProperties.size());
            userDetails.put("savedPropertiesCount", savedCount);

            return ResponseEntity.ok(userDetails);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Create admin user (super admin functionality)
     */
    @PostMapping("/users/create-admin")
    public ResponseEntity<?> createAdminUser(@RequestBody Map<String, String> userData) {
        try {
            String email = userData.get("email");

            // Check if user already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("{\n" +
                        "  \"success\": false,\n" +
                        "  \"message\": \"User with this email already exists\"\n" +
                        "}");
            }

            User admin = new User();
            admin.setFirstName(userData.get("firstName"));
            admin.setLastName(userData.get("lastName"));
            admin.setEmail(email);
            admin.setPassword(userData.get("password")); // In real app, this should be hashed
            admin.setPhone(userData.get("phone"));
            admin.setRole("ADMIN");
            admin.setIsActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            User savedAdmin = userRepository.save(admin);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Admin user created successfully\",\n" +
                    "  \"userId\": " + savedAdmin.getUserId() + "\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to create admin user: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }
}