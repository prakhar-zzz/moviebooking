package com.example.movieticket.controller;

import com.example.movieticket.dto.BookingRequest;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.User;
import com.example.movieticket.repository.UserRepository;
import com.example.movieticket.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request, Authentication auth) {
        try {
            // Optionally override userId from authenticated principal for security:
            // If token contains userId claim you can rely on request.getUserId(), else find by username
            Long userId = request.getUserId();
            if (userId == null && auth != null) {
                User u = userRepository.findByUsername(auth.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                userId = u.getId();
            }
            Booking booking = bookingService.createBooking(
                    userId,
                    request.getShowId(),
                    request.getSeatNumbers()
            );
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId, Authentication auth) {
        try {
            // check ownership (unless admin)
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_ADMIN"));

            if (!isAdmin) {
                Booking booking = bookingService.getBookingById(bookingId);
                User user = userRepository.findByUsername(auth.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                if (booking.getUser() == null || !booking.getUser().getId().equals(user.getId())) {
                    return ResponseEntity.status(403).body("Forbidden");
                }
            }

            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Booking canceled successfully: " + bookingId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBooking(@PathVariable Long bookingId, Authentication auth) {
        try {
            Booking booking = bookingService.getBookingById(bookingId);

            // if not admin, ensure owner
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_ADMIN"));

            if (!isAdmin) {
                User user = userRepository.findByUsername(auth.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                if (booking.getUser() == null || !booking.getUser().getId().equals(user.getId())) {
                    return ResponseEntity.status(403).body("Forbidden");
                }
            }

            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingsForUser(@PathVariable Long userId, Authentication auth) {
        try {
            // allow admin OR owner
            boolean isAdmin = auth != null && auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_ADMIN"));

            if (!isAdmin) {
                User user = userRepository.findByUsername(auth.getName())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                if (!user.getId().equals(userId)) {
                    return ResponseEntity.status(403).body("Forbidden");
                }
            }

            List<Booking> bookings = bookingService.getBookingsForUser(userId);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
