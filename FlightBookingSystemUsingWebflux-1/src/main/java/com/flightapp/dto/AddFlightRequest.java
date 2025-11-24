package com.flightapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class AddFlightRequest {

    @NotBlank
    private String flightNumber;

    @NotBlank
    private String airlineId;

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotNull
    private Instant departureTime;

    @NotNull
    private Instant arrivalTime;

    @NotNull
    @Min(1)
    private Integer totalSeats;

    @NotNull
    @Positive
    private Double baseFare;
}
