package com.example.movieticket.repository;

import com.example.movieticket.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovie_Id(Long movieId);
}
