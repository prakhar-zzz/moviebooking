package com.example.movieticket.service;

import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Seat;
import com.example.movieticket.model.Show;
import com.example.movieticket.repository.MovieRepository;
import com.example.movieticket.repository.SeatRepository;
import com.example.movieticket.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;

    public ShowService(ShowRepository showRepository, SeatRepository seatRepository, MovieRepository movieRepository) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public Show createShow(Long movieId, Show showPayload) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        Show show = new Show();
        show.setMovie(movie);
        show.setTotalSeats(showPayload.getTotalSeats() == 0 ? 100 : showPayload.getTotalSeats());
        show.setAvailableSeats(showPayload.getAvailableSeats() == 0 ? show.getTotalSeats() : showPayload.getAvailableSeats());
        show.setShowTime(showPayload.getShowTime());

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

        savedShow.setSeats(seats);
        return savedShow;
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public Show getShowById(Long id) {
        return showRepository.findById(id).orElse(null);
    }

    public List<Show> getShowsByMovie(Long movieId) {
        return showRepository.findByMovie_Id(movieId);
    }
}
