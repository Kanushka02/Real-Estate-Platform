package com.srilanka.realestate.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.srilanka.realestate.entity.PropertyImage;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.PropertyImageService;

@RestController
@RequestMapping("/api/property-images")
@CrossOrigin(origins = "http://localhost:3000")
public class PropertyImageController {

    @Autowired
    private PropertyImageService propertyImageService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Upload images for a property (SELLER and ADMIN only)
     */
    @PostMapping("/upload/{propertyId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadImages(@PathVariable Long propertyId,
                                         @RequestParam("images") MultipartFile[] files,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            List<PropertyImage> uploadedImages = propertyImageService.uploadImages(propertyId, files, userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Images uploaded successfully\",\n" +
                    "  \"uploadedCount\": " + uploadedImages.size() + ",\n" +
                    "  \"images\": " + uploadedImages.toString() + "\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to upload images: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Get all images for a property (public access)
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<PropertyImage>> getPropertyImages(@PathVariable Long propertyId) {
        List<PropertyImage> images = propertyImageService.getPropertyImages(propertyId);
        return ResponseEntity.ok(images);
    }

    /**
     * Delete an image (Owner or Admin only)
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            propertyImageService.deleteImage(imageId, userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Image deleted successfully\"\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to delete image: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Set primary image for a property (Owner or Admin only)
     */
    @PutMapping("/{imageId}/set-primary")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> setPrimaryImage(@PathVariable Long imageId,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            propertyImageService.setPrimaryImage(imageId, userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Primary image updated successfully\"\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to set primary image: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Update image sort order (Owner or Admin only)
     */
    @PutMapping("/{imageId}/order")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateImageOrder(@PathVariable Long imageId,
                                             @RequestParam Integer sortOrder,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            propertyImageService.updateImageOrder(imageId, sortOrder, userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Image order updated successfully\"\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to update image order: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Helper method to extract email from Authorization header
     */
    private String getUserEmailFromAuth(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUsernameFromToken(token);
        }
        throw new RuntimeException("Invalid authorization header");
    }
}