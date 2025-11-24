package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.flightapp.model.enums.SeatClass;
import com.flightapp.model.enums.SeatStatus;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "seats")
public class Seat {

    @Id
    private String id;

    @NotBlank
    private String flightId;

    @NotBlank
    private String seatNumber;

    private SeatClass seatClass;

    private SeatStatus status;

    private String heldBy;

    private Instant holdExpiresAt;

    private String bookingRef;
}
