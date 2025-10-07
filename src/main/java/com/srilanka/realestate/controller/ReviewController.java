package com.srilanka.realestate.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.entity.Review;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create a new review for a property
     */
    @PostMapping("/property/{propertyId}")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> createReview(@PathVariable Long propertyId,
                                         @RequestBody CreateReviewRequest request,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            Review review = reviewService.createReview(propertyId, request.getRating(), request.getComment(), userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Review created successfully\",\n" +
                    "  \"reviewId\": " + review.getReviewId() + "\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to create review: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Update an existing review
     */
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                         @RequestBody CreateReviewRequest request,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            Review review = reviewService.updateReview(reviewId, request.getRating(), request.getComment(), userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Review updated successfully\",\n" +
                    "  \"reviewId\": " + review.getReviewId() + "\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to update review: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Delete a review
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            reviewService.deleteReview(reviewId, userEmail);

            return ResponseEntity.ok().body("{\n" +
                    "  \"success\": true,\n" +
                    "  \"message\": \"Review deleted successfully\"\n" +
                    "}");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\n" +
                    "  \"success\": false,\n" +
                    "  \"message\": \"Failed to delete review: " + e.getMessage() + "\"\n" +
                    "}");
        }
    }

    /**
     * Get all reviews for a property (public access)
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Review>> getReviewsForProperty(@PathVariable Long propertyId) {
        List<Review> reviews = reviewService.getReviewsForProperty(propertyId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get average rating for a property (public access)
     */
    @GetMapping("/property/{propertyId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForProperty(@PathVariable Long propertyId) {
        Double averageRating = reviewService.getAverageRatingForProperty(propertyId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }

    /**
     * Get review count for a property (public access)
     */
    @GetMapping("/property/{propertyId}/count")
    public ResponseEntity<Long> getReviewCountForProperty(@PathVariable Long propertyId) {
        long count = reviewService.getReviewCountForProperty(propertyId);
        return ResponseEntity.ok(count);
    }

    /**
     * Get reviews with minimum rating (public access)
     */
    @GetMapping("/property/{propertyId}/rating/{minRating}")
    public ResponseEntity<List<Review>> getReviewsWithMinimumRating(@PathVariable Long propertyId,
                                                                   @PathVariable Integer minRating) {
        List<Review> reviews = reviewService.getReviewsWithMinimumRating(propertyId, minRating);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get current user's reviews
     */
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<List<Review>> getMyReviews(@RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            List<Review> reviews = reviewService.getReviewsByUser(userEmail);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Check if user can review a property
     */
    @GetMapping("/property/{propertyId}/can-review")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Boolean> canUserReviewProperty(@PathVariable Long propertyId,
                                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            boolean canReview = reviewService.canUserReviewProperty(userEmail, propertyId);
            return ResponseEntity.ok(canReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user's review for a specific property
     */
    @GetMapping("/property/{propertyId}/my-review")
    @PreAuthorize("hasRole('BUYER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<Review> getMyReviewForProperty(@PathVariable Long propertyId,
                                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String userEmail = getUserEmailFromAuth(authHeader);
            Optional<Review> review = reviewService.getUserReviewForProperty(userEmail, propertyId);
            return review.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get recent reviews (admin only)
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Review>> getRecentReviews() {
        List<Review> reviews = reviewService.getRecentReviews();
        return ResponseEntity.ok(reviews);
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

    /**
     * DTO for review creation/update requests
     */
    public static class CreateReviewRequest {
        private Integer rating;
        private String comment;

        public CreateReviewRequest() {}

        public CreateReviewRequest(Integer rating, String comment) {
            this.rating = rating;
            this.comment = comment;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}