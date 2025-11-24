package com.flightapp.controller;

import com.flightapp.model.Seat;
import com.flightapp.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight/{flightId}/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public Flux<Seat> getSeats(@PathVariable String flightId) {
        return seatService.getSeatsForFlight(flightId);
    }

    @GetMapping("/{seatNumber}")
    public Mono<ResponseEntity<Seat>> getSeat(@PathVariable String flightId, @PathVariable String seatNumber) {
        return seatService.getSeat(flightId, seatNumber)
                .map(s -> ResponseEntity.ok(s))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
