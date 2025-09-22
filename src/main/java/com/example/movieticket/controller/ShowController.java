package com.example.movieticket.controller;

import com.example.movieticket.model.Show;
import com.example.movieticket.service.ShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    // Create show for a movie
    @PostMapping("/movies/{movieId}/shows")
    public ResponseEntity<?> addShow(@PathVariable Long movieId, @RequestBody Show show) {
        try {
            Show saved = showService.createShow(movieId, show);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get shows for a movie (list)
    @GetMapping("/movies/{movieId}/shows")
    public ResponseEntity<List<Show>> getShowsForMovie(@PathVariable Long movieId) {
        List<Show> shows = showService.getShowsByMovie(movieId);
        return ResponseEntity.ok(shows);
    }

    // NEW: Get a single show by its id (includes seats)
    @GetMapping("/shows/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        if (show == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(show);
    }
}
