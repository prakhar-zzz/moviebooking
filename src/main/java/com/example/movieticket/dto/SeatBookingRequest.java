package com.example.movieticket.dto;

import java.util.List;

public class SeatBookingRequest {
    private List<String> seatNumbers;

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }
}
