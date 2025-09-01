package com.example.movieticket.service;

import com.example.movieticket.model.Seat;
import com.example.movieticket.model.Show;
import com.example.movieticket.repository.SeatRepository;
import com.example.movieticket.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SeatRepository seatRepository;

    public Show createShow(Show show) {
        // Set initial values
        if (show.getTotalSeats() == 0) {
            show.setTotalSeats(100);
        }
        show.setAvailableSeats(show.getTotalSeats());

        // Save show first
        Show savedShow = showRepository.save(show);

        // Generate seats
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= savedShow.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setSeatNumber("S" + i);
            seat.setBooked(false);
            seat.setShow(savedShow);
            seats.add(seat);
        }

        seatRepository.saveAll(seats);

        // Reload show with seats
        savedShow.setSeats(seats);
        return savedShow;
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id).orElse(null);
    }
}
