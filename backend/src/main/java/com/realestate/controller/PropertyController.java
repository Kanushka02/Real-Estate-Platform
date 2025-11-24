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
import org.springframework.http.MediaType;

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
    
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getProperty(@PathVariable Long id) {
        PropertyDTO property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
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
    public ResponseEntity<Page<PropertyDTO>> searchProperties(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PropertyDTO> properties = propertyService.searchProperties(keyword, page, size);
        return ResponseEntity.ok(properties);
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
        
        Page<PropertyDTO> properties = propertyService.filterProperties(
                type, listingType, district, city, minPrice, maxPrice, minBedrooms, page, size);
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<List<PropertyDTO>> getLatestProperties() {
        List<PropertyDTO> properties = propertyService.getLatestProperties();
        return ResponseEntity.ok(properties);
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<PropertyDTO>> getFeaturedProperties() {
        List<PropertyDTO> properties = propertyService.getFeaturedProperties();
        return ResponseEntity.ok(properties);
    }

    @PostMapping("/{id}/image")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public ResponseEntity<?> uploadPropertyImage(
        @PathVariable Long id,
        @RequestParam("image") MultipartFile imageFile,
        Authentication authentication) {
    try {
        String username = authentication.getName();
        PropertyDTO updatedProperty = propertyService.uploadPropertyImage(id, imageFile, username);
        return ResponseEntity.ok(updatedProperty);
    } catch (IOException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to upload image: " + e.getMessage());
    }
}

@GetMapping("/{id}/image")
public ResponseEntity<?> getPropertyImage(@PathVariable Long id) {
    try {
        PropertyDTO property = propertyService.getPropertyById(id);
        if (property != null && property.imageData != null) {
            return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(property.imageType))
                .body(property.imageData);
        }
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to retrieve image: " + e.getMessage());
    }
}
    
    // // Photo upload endpoints
    // @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // public ResponseEntity<PropertyDTO> uploadPhoto(@PathVariable Long id,
    //                                                @RequestParam("file") MultipartFile file,
    //                                                @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
    //                                                Authentication authentication) {
    //     String username = authentication.getName();
    //     PropertyDTO updated = propertyService.uploadPhoto(id, file, isPrimary, username);
    //     return ResponseEntity.ok(updated);
    // }
    
    // @GetMapping("/{id}/photos/{photoId}")
    // public ResponseEntity<byte[]> getPhoto(@PathVariable Long id, @PathVariable Long photoId) {
    //     byte[] imageData = propertyService.getPhotoData(id, photoId);
    //     return ResponseEntity.ok()
    //             .contentType(MediaType.IMAGE_JPEG) // Will be determined dynamically in service
    //             .body(imageData);
    // }
    
    // @DeleteMapping("/{id}/photos/{photoId}")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // public ResponseEntity<?> deletePhoto(@PathVariable Long id, 
    //                                     @PathVariable Long photoId,
    //                                     Authentication authentication) {
    //     String username = authentication.getName();
    //     propertyService.deletePhoto(id, photoId, username);
    //     return ResponseEntity.ok().build();
    // }
    
    // @PutMapping("/{id}/photos/{photoId}/primary")
    // @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    // public ResponseEntity<PropertyDTO> setPrimaryPhoto(@PathVariable Long id,
    //                                                   @PathVariable Long photoId,
    //                                                   Authentication authentication) {
    //     String username = authentication.getName();
    //     PropertyDTO updated = propertyService.setPrimaryPhoto(id, photoId, username);
    //     return ResponseEntity.ok(updated);
    // }
}

