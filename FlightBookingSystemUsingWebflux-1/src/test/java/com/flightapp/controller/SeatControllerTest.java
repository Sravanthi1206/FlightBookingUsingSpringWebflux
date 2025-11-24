package com.flightapp.controller;

import com.flightapp.model.Seat;
import com.flightapp.model.enums.SeatStatus;
import com.flightapp.service.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = SeatController.class)
@ImportAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
class SeatControllerTest {

    @Autowired WebTestClient webClient;
    @MockBean SeatService seatService;

    @Test
    void getSeats_returnsFluxOfSeats() {
        Seat s = new Seat(); s.setId("s1"); s.setSeatNumber("1A");
        when(seatService.getSeatsForFlight("f1")).thenReturn(Flux.just(s));

        webClient.get().uri("/api/flight/f1/seats")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].seatNumber").isEqualTo("1A");
    }

    @Test
    void getSeat_returnsSeatWhenFound() {
        Seat s = new Seat(); s.setId("s1"); s.setSeatNumber("1A"); s.setStatus(SeatStatus.AVAILABLE);
        when(seatService.getSeat("f1","1A")).thenReturn(Mono.just(s));

        webClient.get().uri("/api/flight/f1/seats/1A")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.seatNumber").isEqualTo("1A")
                .jsonPath("$.status").isEqualTo("AVAILABLE");
    }
}
