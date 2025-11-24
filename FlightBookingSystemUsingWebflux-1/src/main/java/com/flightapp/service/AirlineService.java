package com.flightapp.service;

import com.flightapp.dto.AddAirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Airline;
import com.flightapp.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository repo;

    public Mono<AirlineResponse> addAirline(AddAirlineRequest req) {

        Airline airline = Airline.builder()
                .airlineCode(req.getAirlineCode())
                .airlineName(req.getAirlineName())
                .build();

        return repo.save(airline)     
                .map(saved -> AirlineResponse.builder()
                        .id(saved.getId())
                        .airlineCode(saved.getAirlineCode())
                        .airlineName(saved.getAirlineName())
                        .build());
    }
    
    public Mono<AirlineResponse> getAirline(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Airline not found")))
                .map(a -> AirlineResponse.builder()
                        .id(a.getId())
                        .airlineCode(a.getAirlineCode())
                        .airlineName(a.getAirlineName())
                        .build());
    }
}
