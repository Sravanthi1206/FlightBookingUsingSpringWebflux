package com.flightapp.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDTO {

    @NotBlank
    private String name;

    @Min(1)
    private int age;
}
