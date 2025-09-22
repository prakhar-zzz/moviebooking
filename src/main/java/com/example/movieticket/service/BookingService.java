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

    @Transactional
    public Booking createBooking(Long userId, Long showId, List<String> seatNumbers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        List<Seat> seats = seatRepository.findByShow_IdAndSeatNumberIn(showId, seatNumbers);

        if (seats.size() != seatNumbers.size()) {
            throw new RuntimeException("One or more seats not found in this show");
        }

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                throw new RuntimeException("Seat already booked: " + seat.getSeatNumber());
            }
        }

        seats.forEach(seat -> seat.setBooked(true));
        seatRepository.saveAll(seats);

        show.setAvailableSeats(show.getAvailableSeats() - seats.size());
        showRepository.save(show);

        Booking booking = new Booking();
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setCustomerName(user.getUsername());
        booking.setUser(user);        // <-- important
        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.getSeats().forEach(seat -> seat.setBooked(false));
        seatRepository.saveAll(booking.getSeats());

        Show show = booking.getShow();
        show.setAvailableSeats(show.getAvailableSeats() + booking.getSeats().size());
        showRepository.save(show);

        bookingRepository.delete(booking);
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // NEW: return bookings for a user
    public List<Booking> getBookingsForUser(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }
}
