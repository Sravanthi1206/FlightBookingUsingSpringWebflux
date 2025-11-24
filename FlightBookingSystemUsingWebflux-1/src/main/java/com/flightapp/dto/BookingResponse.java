package com.flightapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class BookingResponse {

    private String pnr;
    private String flightId;
    private List<String> seatNumbers;
    private Double amountPaid;
    private String emailId;
    private String status;
    private List<PassengerDTO> passengers;
    private Instant createdAt;
}
