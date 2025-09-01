package com.example.movieticket.service;

import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Seat;
import com.example.movieticket.model.Show;
import com.example.movieticket.model.User;
import com.example.movieticket.repository.BookingRepository;
import com.example.movieticket.repository.SeatRepository;
import com.example.movieticket.repository.ShowRepository;
import com.example.movieticket.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          SeatRepository seatRepository,
                          ShowRepository showRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create booking linked with a user
     */
    @Transactional
    public Booking createBooking(Long userId, Long showId, List<String> seatNumbers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        List<Seat> seats = seatRepository.findByShow_IdAndSeatNumberIn(showId, seatNumbers);

        // Check if all requested seats exist
        if (seats.size() != seatNumbers.size()) {
            throw new RuntimeException("One or more seats not found in this show");
        }

        // Check seat availability
        for (Seat seat : seats) {
            if (seat.isBooked()) {
                throw new RuntimeException("Seat already booked: " + seat.getSeatNumber());
            }
        }

        // Mark seats booked
        seats.forEach(seat -> seat.setBooked(true));
        seatRepository.saveAll(seats);

        // Update show availability
        show.setAvailableSeats(show.getAvailableSeats() - seats.size());
        showRepository.save(show);

        // Save booking
        Booking booking = new Booking();
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setCustomerName(user.getUsername()); // optional, for display
        return bookingRepository.save(booking);
    }

    /**
     * Cancel booking
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Free seats
        booking.getSeats().forEach(seat -> seat.setBooked(false));
        seatRepository.saveAll(booking.getSeats());

        // Update show availability
        Show show = booking.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + booking.getSeats().size());
        showRepository.save(show);

        // Delete booking
        bookingRepository.delete(booking);
    }

    /**
     * Get booking details
     */
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
}
