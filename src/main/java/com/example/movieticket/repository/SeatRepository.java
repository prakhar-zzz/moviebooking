package com.example.movieticket.repository;

import com.example.movieticket.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Get all seats of a show
    List<Seat> findByShow_Id(Long showId);

    // Get specific seats of a show by seat numbers
    List<Seat> findByShow_IdAndSeatNumberIn(Long showId, List<String> seatNumbers);
}
