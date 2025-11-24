package com.flightapp.service;

import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Seat;
import com.flightapp.model.enums.SeatStatus;
import com.flightapp.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @Test
    void getSeatsForFlight_returnsAllSeats() {
        Seat s1 = new Seat();
        s1.setId("s1");
        s1.setFlightId("f1");
        s1.setSeatNumber("1A");
        s1.setStatus(SeatStatus.AVAILABLE);

        Seat s2 = new Seat();
        s2.setId("s2");
        s2.setFlightId("f1");
        s2.setSeatNumber("1B");
        s2.setStatus(SeatStatus.AVAILABLE);

        when(seatRepository.findByFlightId("f1")).thenReturn(Flux.just(s1, s2));

        StepVerifier.create(seatService.getSeatsForFlight("f1"))
                .expectNext(s1)
                .expectNext(s2)
                .verifyComplete();

        verify(seatRepository, times(1)).findByFlightId("f1");
    }

    @Test
    void getSeat_found_returnsSeat() {
        Seat s = new Seat();
        s.setId("s3");
        s.setFlightId("f1");
        s.setSeatNumber("1A");
        s.setStatus(SeatStatus.AVAILABLE);

        when(seatRepository.findByFlightIdAndSeatNumber("f1", "1A")).thenReturn(Mono.just(s));

        StepVerifier.create(seatService.getSeat("f1", "1A"))
                .expectNext(s)
                .verifyComplete();
    }

    @Test
    void getSeat_missing_throwsNotFound() {
        when(seatRepository.findByFlightIdAndSeatNumber("f2", "9Z")).thenReturn(Mono.empty());

        StepVerifier.create(seatService.getSeat("f2", "9Z"))
                .expectErrorSatisfies(err -> {
                    assert err instanceof NotFoundException;
                    assert err.getMessage().contains("Seat not found");
                })
                .verify();
    }
}
