package com.flightapp.controller;

import com.flightapp.dto.AddAirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.service.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight/airline")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping
    public Mono<ResponseEntity<AirlineResponse>> addAirline(@Valid @RequestBody AddAirlineRequest req) {
        return airlineService.addAirline(req)
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AirlineResponse>> getAirline(@PathVariable String id) {
        return airlineService.getAirline(id)
                .map(resp -> ResponseEntity.ok(resp))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
