package com.flightapp.service;

import com.flightapp.dto.AddAirlineRequest;
import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Airline;
import com.flightapp.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    @Test
    void addAirline_shouldSaveAndReturnId() {
        AddAirlineRequest req = new AddAirlineRequest();
        req.setAirlineCode("AI");
        req.setAirlineName("Air India");

        Airline saved = new Airline();
        saved.setId("air-123");
        saved.setAirlineCode("AI");
        saved.setAirlineName("Air India");

        when(airlineRepository.save(any(Airline.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(airlineService.addAirline(req))
                .expectNext("air-123")
                .verifyComplete();

        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    void getAirline_whenFound_returnsResponse() {
        Airline a = new Airline();
        a.setId("a1");
        a.setAirlineCode("AI");
        a.setAirlineName("Air India");

        when(airlineRepository.findById("a1")).thenReturn(Mono.just(a));

        StepVerifier.create(airlineService.getAirline("a1"))
                .assertNext(resp -> {
                    Objects.requireNonNull(resp);
                    assert resp.getId().equals("a1");
                    assert resp.getAirlineCode().equals("AI");
                    assert resp.getAirlineName().equals("Air India");
                })
                .verifyComplete();
    }

    @Test
    void getAirline_whenMissing_throwsNotFound() {
        when(airlineRepository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.getAirline("x"))
                .expectErrorSatisfies(err -> {
                    assert err instanceof NotFoundException;
                    assert err.getMessage().contains("Airline not found");
                }).verify();
    }
}
