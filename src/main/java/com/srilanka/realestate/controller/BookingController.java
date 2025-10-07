package com.srilanka.realestate.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.dto.BookingCreateDTO;
import com.srilanka.realestate.entity.Booking;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:3000")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtUtil jwtUtil;

    private String getUserEmailFromAuth(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.getUsernameFromToken(token);
        }
        throw new RuntimeException("Invalid authorization header");
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public ResponseEntity<Booking> create(@RequestBody BookingCreateDTO dto,
                                          @RequestHeader("Authorization") String authHeader) {
        String email = getUserEmailFromAuth(authHeader);
        return ResponseEntity.ok(bookingService.createBooking(dto, email));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public ResponseEntity<List<Booking>> myBookings(@RequestHeader("Authorization") String authHeader) {
        String email = getUserEmailFromAuth(authHeader);
        return ResponseEntity.ok(bookingService.getMyBookings(email));
    }

    @GetMapping("/my-listings")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<List<Booking>> bookingsForMyListings(@RequestHeader("Authorization") String authHeader) {
        String email = getUserEmailFromAuth(authHeader);
        return ResponseEntity.ok(bookingService.getBookingsForMyListings(email));
    }

    @PatchMapping("/{bookingId}/status")
    public ResponseEntity<Booking> updateStatus(@PathVariable Long bookingId,
                                                @RequestParam String status,
                                                @RequestHeader("Authorization") String authHeader) {
        String email = getUserEmailFromAuth(authHeader);
        return ResponseEntity.ok(bookingService.updateStatus(bookingId, status, email));
    }

    @GetMapping("/between")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> between(@RequestParam LocalDateTime from,
                                                 @RequestParam LocalDateTime to) {
        return ResponseEntity.ok(bookingService.getBetween(from, to));
    }
}



