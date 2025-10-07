package com.srilanka.realestate.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.Review;
import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.PropertyRepository;
import com.srilanka.realestate.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Create a new review
     */
    public Review createReview(Long propertyId, Integer rating, String comment, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check if user has already reviewed this property
        Optional<Review> existingReview = reviewRepository.findByUser_UserIdAndProperty_PropertyId(user.getUserId(), propertyId);
        if (existingReview.isPresent()) {
            throw new RuntimeException("You have already reviewed this property");
        }

        // Validate rating (1-5)
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Review review = new Review(rating, comment, user, property);
        return reviewRepository.save(review);
    }

    /**
     * Update an existing review
     */
    public Review updateReview(Long reviewId, Integer rating, String comment, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check ownership
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException("You can only update your own reviews");
        }

        // Validate rating (1-5)
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    /**
     * Delete a review
     */
    public void deleteReview(Long reviewId, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Check ownership or admin rights
        if (!review.getUser().getUserId().equals(user.getUserId()) && !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    /**
     * Get all reviews for a property
     */
    public List<Review> getReviewsForProperty(Long propertyId) {
        return reviewRepository.findByProperty_PropertyIdOrderByCreatedAtDesc(propertyId);
    }

    /**
     * Get all reviews by a user
     */
    public List<Review> getReviewsByUser(String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        return reviewRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId());
    }

    /**
     * Get average rating for a property
     */
    public Double getAverageRatingForProperty(Long propertyId) {
        return reviewRepository.findAverageRatingByPropertyId(propertyId);
    }

    /**
     * Get review count for a property
     */
    public long getReviewCountForProperty(Long propertyId) {
        return reviewRepository.countByProperty_PropertyId(propertyId);
    }

    /**
     * Get reviews with minimum rating
     */
    public List<Review> getReviewsWithMinimumRating(Long propertyId, Integer minRating) {
        return reviewRepository.findByProperty_PropertyIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(propertyId, minRating);
    }

    /**
     * Get recent reviews (for admin dashboard)
     */
    public List<Review> getRecentReviews() {
        return reviewRepository.findTop10ByOrderByCreatedAtDesc();
    }

    /**
     * Check if user can review property (hasn't reviewed before)
     */
    public boolean canUserReviewProperty(String userEmail, Long propertyId) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Optional<Review> existingReview = reviewRepository.findByUser_UserIdAndProperty_PropertyId(user.getUserId(), propertyId);
        return existingReview.isEmpty();
    }

    /**
     * Get user's review for a specific property
     */
    public Optional<Review> getUserReviewForProperty(String userEmail, Long propertyId) {
        User user = userDetailsService.getUserByEmail(userEmail);
        return reviewRepository.findByUser_UserIdAndProperty_PropertyId(user.getUserId(), propertyId);
    }
}