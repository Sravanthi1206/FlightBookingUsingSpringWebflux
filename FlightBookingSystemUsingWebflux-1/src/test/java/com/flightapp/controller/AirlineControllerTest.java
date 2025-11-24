package com.flightapp.controller;

import com.flightapp.dto.AddAirlineRequest;
import com.flightapp.dto.AirlineResponse;
import com.flightapp.service.AirlineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AirlineController.class)
@ImportAutoConfiguration(exclude = ReactiveSecurityAutoConfiguration.class)
class AirlineControllerTest {

    @Autowired WebTestClient webClient;
    @MockBean AirlineService airlineService;

    @Test
    void addAirline_endToEnd() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setAirlineCode("TA"); req.setAirlineName("TestAir");

        when(airlineService.addAirline(any())).thenReturn(Mono.just("air-1"));

        webClient.post().uri("/api/flight/airline")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("air-1");
    }

    @Test
    void getAirline_found_returnsDto() {
        AirlineResponse resp = AirlineResponse.builder().id("air-1").airlineCode("TA").airlineName("TestAir").build();
        when(airlineService.getAirline("air-1")).thenReturn(Mono.just(resp));

        webClient.get().uri("/api/flight/airline/air-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.airlineName").isEqualTo("TestAir");
    }
}
