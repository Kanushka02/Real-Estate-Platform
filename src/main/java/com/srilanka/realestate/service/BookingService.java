package com.srilanka.realestate.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.srilanka.realestate.dto.BookingCreateDTO;
import com.srilanka.realestate.entity.Booking;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.BookingRepository;
import com.srilanka.realestate.repository.PropertyRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    public Booking createBooking(BookingCreateDTO dto, String buyerEmail) {
        User buyer = userDetailsService.getUserByEmail(buyerEmail);
        if (!buyer.getRole().equals("BUYER") && !buyer.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("Only buyers or admins can create bookings");
        }

        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Booking booking = new Booking();
        booking.setBuyer(buyer);
        booking.setProperty(property);
        booking.setScheduledAt(dto.getScheduledAt());
        booking.setMessage(dto.getMessage());
        booking.setStatus("PENDING");
        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String buyerEmail) {
        User buyer = userDetailsService.getUserByEmail(buyerEmail);
        return bookingRepository.findByBuyer(buyer);
    }

    public List<Booking> getBookingsForMyListings(String sellerEmail) {
        User seller = userDetailsService.getUserByEmail(sellerEmail);
        return bookingRepository.findBySeller(seller);
    }

    public Booking updateStatus(Long bookingId, String newStatus, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Seller of the property or admin can confirm/cancel; buyer can cancel own
        boolean isSeller = booking.getProperty().getSeller().getUserId().equals(user.getUserId());
        boolean isBuyer = booking.getBuyer().getUserId().equals(user.getUserId());
        boolean isAdmin = user.getRole().equals("ADMIN");

        if (!(isSeller || isAdmin || (isBuyer && newStatus.equalsIgnoreCase("CANCELLED")))) {
            throw new AccessDeniedException("Not authorized to update this booking");
        }

        booking.setStatus(newStatus.toUpperCase());
        return bookingRepository.save(booking);
    }

    public List<Booking> getBetween(LocalDateTime from, LocalDateTime to) {
        return bookingRepository.findBetween(from, to);
    }
}



