package com.flightapp.dto;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponse {
    private String id;
    private String flightNumber;
    private String airlineId;
    private String origin;
    private String destination;
    private Instant departureTime;
    private Instant arrivalTime;
    private Integer durationMinutes;
    private Integer totalSeats;
    private Integer availableSeats;
    private Double baseFare;
    private String aircraftType;
}
