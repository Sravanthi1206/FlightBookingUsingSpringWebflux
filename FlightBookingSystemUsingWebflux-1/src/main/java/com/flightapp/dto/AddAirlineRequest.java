package com.flightapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddAirlineRequest {

    @NotBlank
    private String airlineCode;

    @NotBlank
    private String airlineName;
}
