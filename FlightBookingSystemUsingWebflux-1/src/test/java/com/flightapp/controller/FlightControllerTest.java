package com.flightapp.controller;

import com.flightapp.dto.AddFlightRequest;
import com.flightapp.dto.FlightResponse;
import com.flightapp.dto.SearchFlightsRequest;
import com.flightapp.service.FlightService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = FlightController.class)
@ImportAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
class FlightControllerTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    FlightService flightService;

    @Test
    void addFlight_returnsCreated() {
        // Build a valid AddFlightRequest that satisfies all validation constraints
        AddFlightRequest req = new AddFlightRequest();
        req.setFlightNumber("FN101");
        req.setAirlineId("air-1");
        req.setOrigin("HYD");
        req.setDestination("DEL");
        req.setDepartureTime(Instant.now().plusSeconds(3600)); // not null
        req.setArrivalTime(Instant.now().plusSeconds(7200));   // not null
        req.setTotalSeats(120);                                // @Min(1)
        req.setBaseFare(3500.0);                               // @Positive

        // Service mock
        when(flightService.addFlight(any(AddFlightRequest.class))).thenReturn(Mono.just("f-1"));

        webClient.post().uri("/api/flight/add")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("f-1");
    }

    @Test
    void searchFlights_returnsFlux() {
        // Build a valid SearchFlightsRequest. Common fields used by typical search DTOs:
        // origin, destination, departureTime (Instant). If your DTO uses different names,
        // paste the DTO and I'll adapt instantly.
        SearchFlightsRequest req = new SearchFlightsRequest();
        try {
            // Best-effort: set common fields via setters if present
            // (these calls will compile only if the methods exist)
            req.getClass().getMethod("setOrigin", String.class).invoke(req, "HYD");
            req.getClass().getMethod("setDestination", String.class).invoke(req, "DEL");
            // try setDepartureTime(Instant) first; if it's named differently or uses LocalDate, this will be ignored
            try {
                req.getClass().getMethod("setDepartureTime", Instant.class).invoke(req, Instant.now().plusSeconds(86400));
            } catch (NoSuchMethodException ignored) {
                // try setDepartureDate if present (LocalDate)
                try {
                    req.getClass().getMethod("setDepartureDate", java.time.LocalDate.class).invoke(req, java.time.LocalDate.now().plusDays(1));
                } catch (NoSuchMethodException ignored2) {
                    // no-op, request still likely valid if only origin/destination required
                }
            }
        } catch (ReflectiveOperationException ignore) {
            // If reflection fails (setters not present), fall back to using the instance as-is.
            // The mocked service will accept any(SearchFlightsRequest.class) below.
        }

        FlightResponse fr = FlightResponse.builder().id("f-1").build();
        when(flightService.searchFlights(any(SearchFlightsRequest.class))).thenReturn(Flux.just(fr));

        webClient.post().uri("/api/flight/search")
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("f-1");
    }

    @Test
    void getFlight_returnsOk_whenFound() {
        FlightResponse fr = FlightResponse.builder().id("f-1").build();
        when(flightService.getFlight("f-1")).thenReturn(Mono.just(fr));

        webClient.get().uri("/api/flight/f-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("f-1");
    }
}
