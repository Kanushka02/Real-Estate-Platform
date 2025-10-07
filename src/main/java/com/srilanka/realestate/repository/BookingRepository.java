package com.srilanka.realestate.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.srilanka.realestate.entity.Booking;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBuyer(User buyer);

    List<Booking> findByProperty(Property property);

    List<Booking> findByStatus(String status);

    @Query("SELECT b FROM Booking b WHERE b.property.seller = :seller")
    List<Booking> findBySeller(@Param("seller") User seller);

    @Query("SELECT b FROM Booking b WHERE b.scheduledAt >= :from AND b.scheduledAt <= :to")
    List<Booking> findBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}



