package com.flightapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelBookingRequest {
    @NotBlank
    private String reason;
}
