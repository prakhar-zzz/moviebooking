package com.example.movieticket.service;

import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Show;
import com.example.movieticket.model.User;
import com.example.movieticket.repository.BookingRepository;
import com.example.movieticket.repository.ShowRepository;
import com.example.movieticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    // Book seats
    public Booking bookShow(Long userId, Long showId, int seats) {
        // Fetch user
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = optionalUser.get();

        // Fetch show
        Optional<Show> optionalShow = showRepository.findById(showId);
        if (optionalShow.isEmpty()) {
            throw new RuntimeException("Show not found");
        }
        Show show = optionalShow.get();

        if (seats <= 0) {
            throw new RuntimeException("Number of seats must be greater than 0");
        }

        if (show.getAvailableSeats() < seats) {
            throw new RuntimeException("Not enough seats available");
        }

        // Reduce available seats
        show.setAvailableSeats(show.getAvailableSeats() - seats);
        showRepository.save(show);

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);           // now works
        booking.setShow(show);
        booking.setSeatsBooked(seats);

        return bookingRepository.save(booking);
    }

    // Get bookings for a user
    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Cancel booking
    public void cancelBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new RuntimeException("Booking not found");
        }

        Booking booking = optionalBooking.get();
        Show show = booking.getShow();

        // Return seats to show
        show.setAvailableSeats(show.getAvailableSeats() + booking.getSeatsBooked());
        showRepository.save(show);

        // Delete booking
        bookingRepository.delete(booking);
    }
}
