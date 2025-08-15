package com.example.movieticket.controller;

import com.example.movieticket.model.Show;
import com.example.movieticket.repository.ShowRepository;
import com.example.movieticket.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shows")
public class ShowController {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private MovieRepository movieRepository;

    // Get all shows
    @GetMapping
    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    // Add a show
    @PostMapping
    public Show addShow(@RequestBody Show show) {
        // Set total seats
        show.setTotalSeats(100);

        // Initialize available seats to total seats
        show.setAvailableSeats(show.getTotalSeats());

        // Save the show
        return showRepository.save(show);
    }

}
