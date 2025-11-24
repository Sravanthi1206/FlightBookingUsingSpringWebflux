package com.flightapp.repository;

import com.flightapp.model.Flight;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {
    
    Flux<Flight> findByOriginAndDestination(String origin, String destination);
}
