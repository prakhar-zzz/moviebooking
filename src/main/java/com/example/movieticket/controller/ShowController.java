package com.example.movieticket.controller;

import com.example.movieticket.model.Show;
import com.example.movieticket.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shows")
@CrossOrigin(origins = "*")
public class ShowController {

    @Autowired
    private ShowService showService;

    // Get all shows
    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Show> getShowById(@PathVariable Long id) {
        Show show = showService.getShowById(id);
        if (show == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(show);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Show> addShow(@RequestBody Show show) {
        Show savedShow = showService.createShow(show);
        return ResponseEntity.ok(savedShow);
    }
}
