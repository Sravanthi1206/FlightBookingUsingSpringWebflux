package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "airlines")
public class Airline {

    @Id
    private String id;

    @NotBlank
    @Size(min = 2, max = 10)
    private String airlineCode;

    @NotBlank
    @Size(max = 100)
    private String airlineName;

    @Size(max = 512)
    private String logoUrl;

}
