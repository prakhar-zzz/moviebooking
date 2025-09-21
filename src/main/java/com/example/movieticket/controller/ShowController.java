package com.example.movieticket.controller;

import com.example.movieticket.model.Show;
import com.example.movieticket.service.ShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    // ✅ Create show under a movie
    @PostMapping("/movies/{movieId}/shows")
    public ResponseEntity<Show> addShow(@PathVariable Long movieId, @RequestBody Show show) {
        Show savedShow = showService.createShow(movieId, show);
        return ResponseEntity.ok(savedShow);
    }

    // ✅ Get shows by movie
    @GetMapping("/movies/{movieId}/shows")
    public List<Show> getShowsByMovie(@PathVariable Long movieId) {
        return showService.getShowsByMovie(movieId);
    }

    // ✅ Get all shows (optional)
    @GetMapping("/shows")
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }

    // ✅ Get single show
    @GetMapping("/shows/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        if (show == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(show);
    }
}
