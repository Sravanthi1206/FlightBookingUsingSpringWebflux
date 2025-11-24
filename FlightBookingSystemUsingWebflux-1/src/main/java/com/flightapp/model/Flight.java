package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "flights")
public class Flight {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String flightNumber;

    @NotBlank
    private String airlineId;

    @NotBlank
    @Size(max = 5)
    private String origin;

    @NotBlank
    @Size(max = 5)
    private String destination;

    @NotNull
    private Instant departureTime;

    @NotNull
    private Instant arrivalTime;

    @Min(1)
    private Integer durationMinutes;

    @Min(0)
    private Integer totalSeats;

    @Min(0)
    private Integer availableSeats;

    @NotNull
    @PositiveOrZero
    private Double baseFare;

    @Size(max = 100)
    private String aircraftType;
}
