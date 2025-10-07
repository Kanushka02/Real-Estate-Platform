package com.srilanka.realestate.repository;

import com.srilanka.realestate.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find all reviews for a specific property
    List<Review> findByProperty_PropertyIdOrderByCreatedAtDesc(Long propertyId);

    // Find all reviews by a specific user
    List<Review> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    // Check if user has already reviewed a property
    Optional<Review> findByUser_UserIdAndProperty_PropertyId(Long userId, Long propertyId);

    // Get average rating for a property
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.propertyId = :propertyId")
    Double findAverageRatingByPropertyId(@Param("propertyId") Long propertyId);

    // Count reviews for a property
    long countByProperty_PropertyId(Long propertyId);

    // Get reviews with rating above a certain value
    List<Review> findByProperty_PropertyIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(Long propertyId, Integer rating);

    // Get recent reviews across all properties (for admin dashboard)
    List<Review> findTop10ByOrderByCreatedAtDesc();
}