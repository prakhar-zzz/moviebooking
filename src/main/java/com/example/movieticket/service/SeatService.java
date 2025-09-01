package com.example.movieticket.service;

import com.example.movieticket.model.Seat;
import com.example.movieticket.model.Show;
import com.example.movieticket.repository.SeatRepository;
import com.example.movieticket.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    public List<Seat> getSeatsForShow(Long showId) {
        return seatRepository.findByShow_Id(showId);
    }

    public void reserveSeatsBySeatNumbers(Long showId, List<String> seatNumbers) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        List<Seat> seats = seatRepository.findByShow_IdAndSeatNumberIn(showId, seatNumbers);

        if (seats.size() != seatNumbers.size()) {
            throw new RuntimeException("One or more seats not found");
        }

        int bookedCount = 0;
        for (Seat seat : seats) {
            if (seat.isBooked()) throw new RuntimeException("Seat already booked: " + seat.getSeatNumber());
            seat.setBooked(true);
            bookedCount++;
        }

        seatRepository.saveAll(seats);

        show.setAvailableSeats(show.getAvailableSeats() - bookedCount);
        showRepository.save(show);
    }
}
