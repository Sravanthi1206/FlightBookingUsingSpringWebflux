package com.flightapp.repository;

import com.flightapp.model.Airline;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AirlineRepository extends ReactiveMongoRepository<Airline, String> {
    Mono<Airline> findByAirlineCode(String airlineCode);
}
