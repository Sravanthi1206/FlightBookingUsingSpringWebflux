package com.flightapp.service;

import com.flightapp.dto.AddFlightRequest;
import com.flightapp.dto.FlightResponse;
import com.flightapp.dto.SearchFlightsRequest;
import com.flightapp.model.Flight;
import com.flightapp.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository repo;

    public Mono<FlightResponse> addFlight(AddFlightRequest req) {

        int duration = (int) ((req.getArrivalTime().toEpochMilli() - req.getDepartureTime().toEpochMilli()) / 60000);

        Flight f = Flight.builder()
                .flightNumber(req.getFlightNumber())
                .airlineId(req.getAirlineId())
                .origin(req.getOrigin())
                .destination(req.getDestination())
                .departureTime(req.getDepartureTime())
                .arrivalTime(req.getArrivalTime())
                .durationMinutes(duration)
                .totalSeats(req.getTotalSeats())
                .availableSeats(req.getTotalSeats())
                .baseFare(req.getBaseFare())
                .build();

        return repo.save(f).map(this::toResponse);
    }

    public Flux<FlightResponse> searchFlights(SearchFlightsRequest req) {
        return repo.findByOriginAndDestination(req.getOrigin(), req.getDestination())
                .map(this::toResponse);
    }

    private FlightResponse toResponse(Flight f) {
        return FlightResponse.builder()
                .id(f.getId())
                .flightNumber(f.getFlightNumber())
                .airlineId(f.getAirlineId())
                .origin(f.getOrigin())
                .destination(f.getDestination())
                .departureTime(f.getDepartureTime())
                .arrivalTime(f.getArrivalTime())
                .durationMinutes(f.getDurationMinutes())
                .totalSeats(f.getTotalSeats())
                .availableSeats(f.getAvailableSeats())
                .baseFare(f.getBaseFare())
                .build();
    }
}
