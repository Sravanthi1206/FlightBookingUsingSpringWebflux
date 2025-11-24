package com.flightapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PassengerDTO {

    @NotBlank
    private String name;

    @Min(1)
    private int age;
}
