package com.flightapp.controller;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.PassengerDTO;
import com.flightapp.dto.BookingResponse;
import com.flightapp.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = BookingController.class)
@ImportAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
class BookingControllerTest {

    @Autowired
    WebTestClient webClient;

    @MockBean
    BookingService bookingService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createBooking_returnsCreatedPnr() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("f1");
        req.setSeatNumbers(List.of("1A", "1B"));
        req.setAmountPaid(250.0);
        req.setEmailId("user@example.com");

        PassengerDTO p1 = new PassengerDTO();
        p1.setName("Alice");
        p1.setAge(30);
        PassengerDTO p2 = new PassengerDTO();
        p2.setName("Bob");
        p2.setAge(28);
        req.setPassengers(List.of(p1, p2));

        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(Mono.just("PNR1"));

        webClient.post().uri("/api/flight/booking")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("PNR1");
    }

    @Test
    void getBooking_returnsOk_whenFound() {
        BookingResponse resp = BookingResponse.builder().pnr("PNR1").status("CONFIRMED").createdAt(Instant.now()).build();
        when(bookingService.getBooking("PNR1")).thenReturn(Mono.just(resp));

        webClient.get().uri("/api/flight/booking/PNR1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR1");
    }

    @Test
    void bookingHistory_returnsFlux() {
        BookingResponse r = BookingResponse.builder().pnr("P1").build();
        when(bookingService.getBookingsByEmail("u@example.com")).thenReturn(Flux.just(r));

        webClient.get().uri(uri -> uri.path("/api/flight/booking/history").queryParam("email", "u@example.com").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].pnr").isEqualTo("P1");
    }

    @Test
    void cancelBooking_returnsNoContent_whenOk() {
        when(bookingService.cancelBooking("PNR1")).thenReturn(Mono.empty());

        webClient.delete().uri("/api/flight/booking/PNR1")
                .exchange()
                .expectStatus().isNoContent();
    }
}
