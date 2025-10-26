package com.realestate.repository;

import com.realestate.model.Property;
import com.realestate.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    Page<Property> findByStatus(Property.PropertyStatus status, Pageable pageable);
    
    Page<Property> findByOwner(User owner, Pageable pageable);
    
    List<Property> findByOwnerAndStatus(User owner, Property.PropertyStatus status);
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:listingType IS NULL OR p.listingType = :listingType) AND " +
           "(:district IS NULL OR p.district = :district) AND " +
           "(:city IS NULL OR p.city = :city) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Property> findByFilters(
        @Param("type") Property.PropertyType type,
        @Param("listingType") Property.ListingType listingType,
        @Param("district") String district,
        @Param("city") String city,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("minBedrooms") Integer minBedrooms,
        @Param("status") Property.PropertyStatus status,
        Pageable pageable
    );
    
    @Query("SELECT p FROM Property p WHERE p.status = :status AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Property> searchProperties(
        @Param("keyword") String keyword,
        @Param("status") Property.PropertyStatus status,
        Pageable pageable
    );
    
    List<Property> findTop10ByStatusOrderByCreatedAtDesc(Property.PropertyStatus status);
    
    List<Property> findByFeaturedTrueAndStatus(Property.PropertyStatus status);
}

