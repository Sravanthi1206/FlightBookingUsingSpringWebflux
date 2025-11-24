package com.flightapp.model;

import lombok.*;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @NotBlank
    @Size(max = 100)
    private String name;

    @Size(max = 10)
    private String gender;

    @Min(0)
    private Integer age;

    @Size(max = 50)
    private String passportNumber;
}
