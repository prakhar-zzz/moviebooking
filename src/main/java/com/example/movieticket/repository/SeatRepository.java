package com.example.movieticket.repository;

import com.example.movieticket.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShow_Id(Long showId);

    List<Seat> findByShow_IdAndSeatNumberIn(Long showId, List<String> seatNumbers);
}
