package com.flightapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchFlightsRequest {

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;
}
