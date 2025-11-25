package com.realestate.controller;

import com.realestate.dto.PropertyDTO;
import com.realestate.model.Property;
import com.realestate.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;
    
    // ... Keep create, update, delete, get endpoints as they are ...
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyDTO propertyDTO,
                                                      Authentication authentication) {
        String username = authentication.getName();
        PropertyDTO created = propertyService.createProperty(propertyDTO, username);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PropertyDTO> updateProperty(@PathVariable Long id,
                                                      @Valid @RequestBody PropertyDTO propertyDTO,
                                                      Authentication authentication) {
        String username = authentication.getName();
        PropertyDTO updated = propertyService.updateProperty(id, propertyDTO, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        propertyService.deleteProperty(id, username);
        return ResponseEntity.ok().build();
    }

    // --- CHANGED: Upload Multiple Images ---
    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadPropertyImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> files,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            PropertyDTO updatedProperty = propertyService.uploadPropertyImages(id, files, username);
            return ResponseEntity.ok(updatedProperty);
        } catch (IOException e) {
            return ResponseEntity
                .status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to upload images: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<PropertyDTO>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PropertyDTO> properties = propertyService.getAllProperties(page, size);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/my-properties")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<PropertyDTO>> getMyProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        String username = authentication.getName();
        Page<PropertyDTO> properties = propertyService.getMyProperties(username, page, size);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<PropertyDTO>> searchProperties(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(propertyService.searchProperties(keyword, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<PropertyDTO>> filterProperties(
            @RequestParam(required = false) Property.PropertyType type,
            @RequestParam(required = false) Property.ListingType listingType,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(propertyService.filterProperties(type, listingType, district, city, minPrice, maxPrice, minBedrooms, page, size));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<PropertyDTO>> getLatestProperties() {
        return ResponseEntity.ok(propertyService.getLatestProperties());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<PropertyDTO>> getFeaturedProperties() {
        return ResponseEntity.ok(propertyService.getFeaturedProperties());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        PropertyDTO property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }
}