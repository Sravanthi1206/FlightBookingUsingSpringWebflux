package com.flightapp.service;

import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Seat;
import com.flightapp.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    // Get all seats for a flight
    public Flux<Seat> getSeatsForFlight(String flightId) {
        return seatRepository.findByFlightId(flightId);
    }

    // Get specific seat
    public Mono<Seat> getSeat(String flightId, String seatNumber) {
        return seatRepository.findByFlightIdAndSeatNumber(flightId, seatNumber)
                .switchIfEmpty(Mono.error(new NotFoundException("Seat not found")));
    }
}
