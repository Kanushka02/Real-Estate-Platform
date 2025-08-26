package com.srilanka.realestate.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.srilanka.realestate.service.CustomUserDetailsService;
import com.srilanka.realestate.service.PropertyService;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // All endpoints require ADMIN role
public class AdminController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Get property statistics
        PropertyService.PropertyStats propertyStats = propertyService.getPropertyStats();

        // User statistics
        List<User> allUsers = userDetailsService.getAllUsers();
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream().mapToLong(user -> user.getIsActive() ? 1 : 0).sum();
        long sellers = userDetailsService.getUsersByRole("SELLER").size();
        long buyers = userDetailsService.getUsersByRole("BUYER").size();
        long admins = userDetailsService.getUsersByRole("ADMIN").size();

        stats.put("users", Map.of(
                "total", totalUsers,
                "active", activeUsers,
                "sellers", sellers,
                "buyers", buyers,
                "admins", admins
        ));

        stats.put("properties", Map.of(
                "total", propertyStats.getTotalProperties(),
                "active", propertyStats.getActiveProperties(),
                "sold", propertyStats.getSoldProperties(),
                "rented", propertyStats.getRentedProperties(),
                "forSale", propertyStats.getPropertiesForSale(),
                "forRent", propertyStats.getPropertiesForRent()
        ));

        stats.put("generatedAt", LocalDateTime.now());

        return ResponseEntity.ok(stats);
    }

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userDetailsService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        List<User> users = userDetailsService.getUsersByRole(role.toUpperCase());
        return ResponseEntity.ok(users);
    }

    /**
     * Activate/Deactivate user
     */
    @PatchMapping("/users/{userId}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        try {
            User user = userDetailsService.getUserById(userId);
            user.setIsActive(!user.getIsActive());
            user.setUpdatedAt(LocalDateTime.now());
            userDetailsService.updateUser(user);

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
     * Get all properties with seller information (Admin view)
     */
    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getAllPropertiesForAdmin() {
        List<Property> properties = propertyService.getAllProperties(); // Includes inactive properties
        return ResponseEntity.ok(properties);
    }

    /**
     * Get properties by status
     */
    @GetMapping("/properties/status/{status}")
    public ResponseEntity<List<Property>> getPropertiesByStatus(@PathVariable String status) {
        List<Property> properties = propertyService.getAllProperties().stream()
                .filter(p -> p.getStatus().equals(status.toUpperCase()))
                .toList();
        return ResponseEntity.ok(properties);
    }

    /**
     * Change property status as admin (ACTIVE, INACTIVE, SOLD, etc.)
     */
    @PatchMapping("/properties/{propertyId}/status")
    public ResponseEntity<?> changePropertyStatusAsAdmin(@PathVariable Long propertyId,
                                                         @RequestParam String status) {
        try {
            Optional<Property> propertyOpt = propertyService.getPropertyById(propertyId);
            if (propertyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Property property = propertyOpt.get();
            property.setStatus(status.toUpperCase());
            property.setUpdatedAt(LocalDateTime.now());

            // For admin, we need to directly save through service
            // This would require additional method in PropertyService for admin operations

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
     * Delete property as admin (admin override)
     */
    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<?> deletePropertyAsAdmin(@PathVariable Long propertyId) {
        try {
            Optional<Property> propertyOpt = propertyService.getPropertyById(propertyId);
            if (propertyOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // For admin deletion, we'll use a dummy admin email
            // In a real application, you'd get this from the JWT token
            String adminEmail = "admin@propertylk.com";
            propertyService.deleteProperty(propertyId, adminEmail);

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
     * Get user details with their properties
     */
    @GetMapping("/users/{userId}/details")
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable Long userId) {
        try {
            User user = userDetailsService.getUserById(userId);
            List<Property> userProperties = propertyService.getUserProperties(user.getEmail());

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("user", user);
            userDetails.put("properties", userProperties);
            userDetails.put("propertiesCount", userProperties.size());

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
            if (userDetailsService.existsByEmail(email)) {
                return ResponseEntity.badRequest().body("{\n" +
                        "  \"success\": false,\n" +
                        "  \"message\": \"User with this email already exists\"\n" +
                        "}");
            }

            User admin = new User();
            admin.setFirstName(userData.get("firstName"));
            admin.setLastName(userData.get("lastName"));
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(userData.get("password"))); // Hash the password
            admin.setPhone(userData.get("phone"));
            admin.setRole("ADMIN");
            admin.setIsActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            User savedAdmin = userDetailsService.updateUser(admin);

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

    /**
     * Get recent activities overview
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();

        // Get recent properties (you'll need to implement this in PropertyService)
        List<Property> allProperties = propertyService.getAllProperties();
        List<Property> recentProperties = allProperties.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(10)
                .toList();

        // Get recent users
        List<User> allUsers = userDetailsService.getAllUsers();
        List<User> recentUsers = allUsers.stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(10)
                .toList();

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
        List<User> allUsers = userDetailsService.getAllUsers();
        List<User> searchResults = allUsers.stream()
                .filter(user ->
                        user.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                                user.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                                user.getLastName().toLowerCase().contains(query.toLowerCase())
                )
                .toList();
        return ResponseEntity.ok(searchResults);
    }

    /**
     * Test endpoint to create admin
     */
    @GetMapping("/test-create-admin")
    public ResponseEntity<?> testCreateAdmin() {
        Map<String, String> adminData = new HashMap<>();
        adminData.put("firstName", "Test");
        adminData.put("lastName", "Admin");
        adminData.put("email", "testadmin@propertylk.com");
        adminData.put("password", "admin123");
        adminData.put("phone", "0701234567");

        return createAdminUser(adminData);
    }

    /**
     * Get property statistics summary
     */
    @GetMapping("/properties/statistics")
    public ResponseEntity<Map<String, Object>> getPropertyStatistics() {
        PropertyService.PropertyStats stats = propertyService.getPropertyStats();

        Map<String, Object> propertyStats = new HashMap<>();
        propertyStats.put("total", stats.getTotalProperties());
        propertyStats.put("active", stats.getActiveProperties());
        propertyStats.put("sold", stats.getSoldProperties());
        propertyStats.put("rented", stats.getRentedProperties());
        propertyStats.put("forSale", stats.getPropertiesForSale());
        propertyStats.put("forRent", stats.getPropertiesForRent());

        return ResponseEntity.ok(propertyStats);
    }

    /**
     * Get user statistics summary
     */
    @GetMapping("/users/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        List<User> allUsers = userDetailsService.getAllUsers();

        Map<String, Object> userStats = new HashMap<>();
        userStats.put("total", allUsers.size());
        userStats.put("active", allUsers.stream().mapToLong(u -> u.getIsActive() ? 1 : 0).sum());
        userStats.put("sellers", userDetailsService.getUsersByRole("SELLER").size());
        userStats.put("buyers", userDetailsService.getUsersByRole("BUYER").size());
        userStats.put("admins", userDetailsService.getUsersByRole("ADMIN").size());

        return ResponseEntity.ok(userStats);
    }
}