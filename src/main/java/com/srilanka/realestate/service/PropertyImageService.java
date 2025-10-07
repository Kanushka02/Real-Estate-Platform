package com.srilanka.realestate.service;

import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.PropertyImage;
import com.srilanka.realestate.repository.PropertyImageRepository;
import com.srilanka.realestate.repository.PropertyRepository;

@Service
public class PropertyImageService {

    private static final String UPLOAD_DIR = "uploads/property-images/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/webp"};

    @Autowired
    private PropertyImageRepository propertyImageRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Upload multiple images for a property
     */
    @Transactional
    public List<PropertyImage> uploadImages(Long propertyId, MultipartFile[] files, String userEmail) {
        // Validate user owns the property
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only upload images for your own properties");
        }

        // Create upload directory if it doesn't exist
        createUploadDirectory();

        List<PropertyImage> uploadedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                validateFile(file);

                try {
                    String fileName = generateUniqueFileName(file.getOriginalFilename());
                    Path filePath = Paths.get(UPLOAD_DIR + fileName);

                    // Save file to disk
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                    // Save to database
                    PropertyImage propertyImage = new PropertyImage(
                        "/api/uploads/property-images/" + fileName,
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        property
                    );

                    // If this is the first image, make it primary
                    if (propertyImageRepository.countByProperty_PropertyId(propertyId) == 0) {
                        propertyImage.setIsPrimary(true);
                    }

                    PropertyImage savedImage = propertyImageRepository.save(propertyImage);
                    uploadedImages.add(savedImage);

                } catch (IOException e) {
                    throw new RuntimeException("Failed to save image: " + file.getOriginalFilename(), e);
                }
            }
        }

        return uploadedImages;
    }

    /**
     * Get all images for a property
     */
    public List<PropertyImage> getPropertyImages(Long propertyId) {
        return propertyImageRepository.findByProperty_PropertyIdOrderBySortOrderAsc(propertyId);
    }

    /**
     * Delete an image
     */
    @Transactional
    public void deleteImage(Long imageId, String userEmail) {
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Validate user owns the property
        if (!image.getProperty().getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only delete images from your own properties");
        }

        // Delete file from disk
        try {
            Path filePath = Paths.get(UPLOAD_DIR + getFileNameFromUrl(image.getImageUrl()));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log error but continue with database deletion
            System.err.println("Failed to delete file: " + image.getImageUrl());
        }

        // If this was the primary image, make another image primary
        if (image.getIsPrimary()) {
            List<PropertyImage> remainingImages = propertyImageRepository
                .findByProperty_PropertyIdOrderBySortOrderAsc(image.getProperty().getPropertyId());
            if (remainingImages.size() > 1) {
                // Make the first remaining image primary
                PropertyImage newPrimary = remainingImages.stream()
                    .filter(img -> !img.getImageId().equals(imageId))
                    .findFirst()
                    .orElse(null);
                if (newPrimary != null) {
                    newPrimary.setIsPrimary(true);
                    propertyImageRepository.save(newPrimary);
                }
            }
        }

        // Delete from database
        propertyImageRepository.delete(image);
    }

    /**
     * Set primary image for a property
     */
    @Transactional
    public void setPrimaryImage(Long imageId, String userEmail) {
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Validate user owns the property
        if (!image.getProperty().getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only modify images from your own properties");
        }

        // Set all images for this property as non-primary
        propertyImageRepository.setAllImagesAsNonPrimary(image.getProperty().getPropertyId());

        // Set this image as primary
        image.setIsPrimary(true);
        propertyImageRepository.save(image);
    }

    /**
     * Update image sort order
     */
    @Transactional
    public void updateImageOrder(Long imageId, Integer sortOrder, String userEmail) {
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Validate user owns the property
        if (!image.getProperty().getSeller().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only modify images from your own properties");
        }

        image.setSortOrder(sortOrder);
        propertyImageRepository.save(image);
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB");
        }

        // Check file type
        String contentType = file.getContentType();
        boolean isAllowedType = false;
        for (String allowedType : ALLOWED_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowedType = true;
                break;
            }
        }

        if (!isAllowedType) {
            throw new IllegalArgumentException("File type not allowed. Only JPEG, PNG, and WebP images are accepted");
        }
    }

    /**
     * Generate unique filename
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex > 0) ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    /**
     * Extract filename from URL
     */
    private String getFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }

    /**
     * Create upload directory if it doesn't exist
     */
    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
}