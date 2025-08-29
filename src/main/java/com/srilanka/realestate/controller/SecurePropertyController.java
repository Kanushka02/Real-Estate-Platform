package com.srilanka.realestate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.dto.PropertyCreateDTO;
import com.srilanka.realestate.entity.Location;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.PropertyCategory;
import com.srilanka.realestate.repository.LocationRepository;
import com.srilanka.realestate.repository.PropertyCategoryRepository;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.PropertyService;

@RestController
@RequestMapping("/api/properties/secure")
@CrossOrigin(origins = "http://localhost:3000")
public class SecurePropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PropertyCategoryRepository categoryRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Helper method to get email from token
    private String getUserEmailFromAuth(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUsernameFromToken(token);
        }
        throw new RuntimeException("Invalid authorization header");
    }

    /**
     * Create a new property (SELLER and ADMIN only)
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> createProperty(@RequestBody PropertyCreateDTO propertyDTO,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            
            // Convert DTO to Entity
            Property property = convertDTOToEntity(propertyDTO);
            
            // Create property through service
            Property createdProperty = propertyService.createProperty(property, userEmail);
            
            return ResponseEntity.ok().body("{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Property created successfully\",\n" +
                "  \"propertyId\": " + createdProperty.getPropertyId() + ",\n" +
                "  \"title\": \"" + createdProperty.getTitle() + "\"\n" +
                "}");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"Failed to create property: " + e.getMessage() + "\"\n" +
                "}");
        }
    }

    /**
     * Get current user's properties
     */
    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<List<Property>> getMyProperties(@RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            List<Property> properties = propertyService.getUserProperties(userEmail);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update property (Owner or Admin only)
     */
    @PutMapping("/update/{propertyId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProperty(@PathVariable Long propertyId,
                                          @RequestBody PropertyCreateDTO propertyDTO,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            Property updatedProperty = convertDTOToEntity(propertyDTO);
            
            Property result = propertyService.updateProperty(propertyId, updatedProperty, userEmail);
            
            return ResponseEntity.ok().body("{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Property updated successfully\",\n" +
                "  \"propertyId\": " + result.getPropertyId() + "\n" +
                "}");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"Failed to update property: " + e.getMessage() + "\"\n" +
                "}");
        }
    }

    /**
     * Delete property (Owner or Admin only)
     */
    @DeleteMapping("/delete/{propertyId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            propertyService.deleteProperty(propertyId, userEmail);
            
            return ResponseEntity.ok().body("{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Property deleted successfully\"\n" +
                "}");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"Failed to delete property: " + e.getMessage() + "\"\n" +
                "}");
        }
    }

    /**
     * Toggle property status (ACTIVE/INACTIVE)
     */
    @PatchMapping("/toggle-status/{propertyId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> togglePropertyStatus(@PathVariable Long propertyId,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            Property property = propertyService.togglePropertyStatus(propertyId, userEmail);
            
            return ResponseEntity.ok().body("{\n" +
                "  \"success\": true,\n" +
                "  \"message\": \"Property status changed to " + property.getStatus() + "\",\n" +
                "  \"newStatus\": \"" + property.getStatus() + "\"\n" +
                "}");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"Failed to toggle status: " + e.getMessage() + "\"\n" +
                "}");
        }
    }

    /**
     * Get all locations for property form dropdowns
     */
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(propertyService.getAllLocations());
    }

    /**
     * Get all categories for property form dropdowns
     */
    @GetMapping("/categories")
    public ResponseEntity<List<PropertyCategory>> getAllCategories() {
        return ResponseEntity.ok(propertyService.getAllCategories());
    }

    /**
     * Test endpoint to create a sample property for authenticated user
     */
    @GetMapping("/test-create")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> testCreateProperty(@RequestHeader("Authorization") String authHeader) {
        try {
            // Create a test property DTO
            PropertyCreateDTO testProperty = new PropertyCreateDTO();
            testProperty.setTitle("Test Property from Secure Endpoint");
            testProperty.setDescription("This property was created using JWT authentication");
            testProperty.setPrice(new java.math.BigDecimal("15000000"));
            testProperty.setPropertyType("SALE");
            testProperty.setBedrooms(2);
            testProperty.setBathrooms(1);
            testProperty.setAreaSqft(1200);
            testProperty.setAddress("Test Address, Colombo");
            testProperty.setLocationId(1L); // Assuming first location exists
            testProperty.setCategoryId(1L); // Assuming first category exists
            
            return createProperty(testProperty, authHeader);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                "  \"success\": false,\n" +
                "  \"message\": \"Test creation failed: " + e.getMessage() + "\"\n" +
                "}");
        }
    }

    /**
     * Helper method to convert DTO to Entity
     */
    private Property convertDTOToEntity(PropertyCreateDTO dto) {
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setPrice(dto.getPrice());
        property.setPropertyType(dto.getPropertyType());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setAreaSqft(dto.getAreaSqft());
        property.setAddress(dto.getAddress());
        
        // Set location and category based on IDs
        if (dto.getLocationId() != null) {
            Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
            property.setLocation(location);
        }
        
        if (dto.getCategoryId() != null) {
            PropertyCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
            property.setCategory(category);
        }
        
        return property;
    }
}