package com.flightapp.repository;

import com.flightapp.model.Seat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SeatRepository extends ReactiveMongoRepository<Seat, String> {
    
    Flux<Seat> findByFlightId(String flightId);

    Mono<Seat> findByFlightIdAndSeatNumber(String flightId, String seatNumber);
}
