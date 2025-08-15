package com.example.movieticket.controller;

import com.example.movieticket.dto.BookingRequest;
import com.example.movieticket.model.Booking;
import com.example.movieticket.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Book a show
    @PostMapping
    public ResponseEntity<?> bookShow(@RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.bookShow(request.getUserId(), request.getShowId(), request.getSeats());
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get user bookings
    @GetMapping("/{userId}")
    public ResponseEntity<List<Booking>> getBookings(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsForUser(userId);
        return ResponseEntity.ok(bookings);
    }

    // Cancel booking
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking canceled successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
