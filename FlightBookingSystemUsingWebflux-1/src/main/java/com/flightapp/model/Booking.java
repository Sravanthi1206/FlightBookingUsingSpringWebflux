package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.flightapp.model.enums.BookingStatus;
import com.flightapp.model.enums.SeatClass;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    @NotBlank
    @Size(max = 64)
    private String pnr;

    @NotBlank
    private String flightId;

    @NotEmpty
    private List<@Valid Passenger> passengers;

    @NotEmpty
    private List<@NotBlank String> seatNumbers;

    @NotNull
    @PositiveOrZero
    private Double amountPaid;

    private SeatClass seatClass;

    @NotBlank
    @Email
    private String emailId;  

    private BookingStatus status;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
