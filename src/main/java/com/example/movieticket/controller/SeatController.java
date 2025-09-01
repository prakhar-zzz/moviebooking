package com.example.movieticket.controller;

import com.example.movieticket.dto.SeatBookingRequest;
import com.example.movieticket.model.Seat;
import com.example.movieticket.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shows")
@CrossOrigin(origins = "*")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    // ✅ Get seats for a show
    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<Seat>> getSeats(@PathVariable Long showId) {
        List<Seat> seats = seatService.getSeatsForShow(showId);
        return ResponseEntity.ok(seats);
    }

    // ✅ Reserve seats by seat numbers
    @PostMapping("/{showId}/reserve")
    public ResponseEntity<String> reserveSeats(
            @PathVariable Long showId,
            @RequestBody SeatBookingRequest request
    ) {
        try {
            seatService.reserveSeatsBySeatNumbers(showId, request.getSeatNumbers());
            return ResponseEntity.ok("Seats booked successfully: " + request.getSeatNumbers());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
