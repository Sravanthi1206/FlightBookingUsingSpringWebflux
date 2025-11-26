package com.flightapp.service;

import com.flightapp.dto.AddFlightRequest;
import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Flight;
import com.flightapp.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;



class FlightServiceTest {

    @Mock
    private FlightRepository flightRepo;

    @InjectMocks
    private FlightService flightService;

    public FlightServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    private AddFlightRequest validAddFlightRequest() {
        AddFlightRequest req = new AddFlightRequest();
        req.setFlightNumber("FN123");
        req.setAirlineId("A1");
        req.setOrigin("HYD");
        req.setDestination("DEL");
        req.setDepartureTime(Instant.now().plusSeconds(3600));
        req.setArrivalTime(Instant.now().plusSeconds(7200));
        req.setTotalSeats(150);
        req.setBaseFare(4500.0);
        return req;
    }

    @Test
    void addFlight_returnsId() {
        AddFlightRequest req = validAddFlightRequest();

        Flight saved = new Flight();
        saved.setId("F1");

        when(flightRepo.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(flightService.addFlight(req))
                .expectNext("F1")
                .verifyComplete();
    }


    @Test
    void getFlight_found_returnsResponse() {
        Flight f = new Flight();
        f.setId("F1");
        f.setFlightNumber("FN123");

        when(flightRepo.findById("F1")).thenReturn(Mono.just(f));

        StepVerifier.create(flightService.getFlight("F1"))
                .expectNextMatches(r -> r.getId().equals("F1"))
                .verifyComplete();
    }

    @Test
    void getFlight_missing_throwsNotFound() {
        when(flightRepo.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(flightService.getFlight("X"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
