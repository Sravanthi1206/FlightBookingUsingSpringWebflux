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

    private final AirlineRepository airlineRepository;

    public Mono<String> addAirline(AddAirlineRequest req) {
        Airline airline = new Airline();
        airline.setAirlineCode(req.getAirlineCode());
        airline.setAirlineName(req.getAirlineName());
        airline.setLogoUrl(null); 

        return airlineRepository.save(airline)
                .map(Airline::getId); 
    }

    
    public Mono<AirlineResponse> getAirline(String id) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Airline not found")))
                .map(a -> AirlineResponse.builder()
                        .id(a.getId())
                        .airlineCode(a.getAirlineCode())
                        .airlineName(a.getAirlineName())
                        .build());
    }
}
