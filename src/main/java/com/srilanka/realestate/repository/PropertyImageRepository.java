package com.srilanka.realestate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srilanka.realestate.entity.PropertyImage;

@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {

    // Find all images for a specific property, ordered by sort order
    List<PropertyImage> findByProperty_PropertyIdOrderBySortOrderAsc(Long propertyId);

    // Find primary image for a property
    PropertyImage findByProperty_PropertyIdAndIsPrimaryTrue(Long propertyId);

    // Count images for a property
    long countByProperty_PropertyId(Long propertyId);

    // Delete all images for a property
    @Modifying
    @Query("DELETE FROM PropertyImage pi WHERE pi.property.propertyId = :propertyId")
    void deleteByPropertyId(@Param("propertyId") Long propertyId);

    // Set all images for a property as non-primary
    @Modifying
    @Query("UPDATE PropertyImage pi SET pi.isPrimary = false WHERE pi.property.propertyId = :propertyId")
    void setAllImagesAsNonPrimary(@Param("propertyId") Long propertyId);

    // Find images by property IDs (for bulk operations)
    List<PropertyImage> findByProperty_PropertyIdIn(List<Long> propertyIds);
}