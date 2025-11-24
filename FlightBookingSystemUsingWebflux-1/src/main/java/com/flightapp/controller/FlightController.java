package com.flightapp.controller;

import com.flightapp.dto.AddFlightRequest;
import com.flightapp.dto.FlightResponse;
import com.flightapp.dto.SearchFlightsRequest;
import com.flightapp.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/add")
    public Mono<ResponseEntity<String>> addFlight(@Valid @RequestBody AddFlightRequest req) {
        return flightService.addFlight(req)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED).body(id));
    }

    @PostMapping("/search")
    public Flux<FlightResponse> searchFlights(@Valid @RequestBody SearchFlightsRequest req) {
        return flightService.searchFlights(req);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<FlightResponse>> getFlight(@PathVariable String id) {
        return flightService.getFlight(id)
                .map(r -> ResponseEntity.ok(r))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
