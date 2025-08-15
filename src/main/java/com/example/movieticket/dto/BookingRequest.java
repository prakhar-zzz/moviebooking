package com.example.movieticket.dto;

public class BookingRequest {
    private Long userId;
    private Long showId;
    private int seats;

    // Getters & Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
}
