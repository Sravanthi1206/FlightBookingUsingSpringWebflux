package com.flightapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookingRequest {

    @NotBlank
    private String flightId;

    @NotEmpty
    private List<@NotBlank String> seatNumbers;

    @NotBlank
    @Email
    private String emailId;

    @NotNull
    @Positive
    private Double amountPaid;

    @NotEmpty
    private List<@Valid Passenger> passengers;
}
