package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.PassengerDTO;
import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.Passenger;
import com.flightapp.model.enums.BookingStatus;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.util.PnrGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private FlightRepository flightRepo;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @Mock
    private PnrGenerator pnrGenerator;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequest sampleRequest() {
        BookingRequest req = new BookingRequest();
        req.setFlightId("flight-1");
        List<String> seats = new ArrayList<>();
        seats.add("1A");
        seats.add("1B");
        req.setSeatNumbers(seats);
        req.setAmountPaid(200.0);
        req.setEmailId("test@example.com");

        List<PassengerDTO> passengers = new ArrayList<>();
        PassengerDTO p1 = new PassengerDTO();
        p1.setName("Alice");
        p1.setAge(30);
        passengers.add(p1);

        PassengerDTO p2 = new PassengerDTO();
        p2.setName("Bob");
        p2.setAge(28);
        passengers.add(p2);

        req.setPassengers(passengers);
        return req;
    }

    @Test
    void createBooking_success_returnsPnr() {
        BookingRequest req = sampleRequest();

        Flight updatedFlight = new Flight();
        updatedFlight.setId("flight-1");
        updatedFlight.setAvailableSeats(98);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Flight.class)))
                .thenReturn(Mono.just(updatedFlight));

        when(pnrGenerator.generate()).thenReturn("PNR1234");

        Booking savedBooking = new Booking();
        savedBooking.setPnr("PNR1234");
        savedBooking.setFlightId("flight-1");
        savedBooking.setSeatNumbers(req.getSeatNumbers());
        savedBooking.setEmailId(req.getEmailId());
        savedBooking.setStatus(BookingStatus.CONFIRMED);
        savedBooking.setCreatedAt(Instant.now());
        savedBooking.setUpdatedAt(Instant.now());

        when(bookingRepo.save(any(Booking.class))).thenReturn(Mono.just(savedBooking));

        StepVerifier.create(bookingService.createBooking(req))
                .expectNext("PNR1234")
                .verifyComplete();

        verify(mongoTemplate, times(1)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Flight.class));
        verify(bookingRepo, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_notEnoughSeats_throws() {
        BookingRequest req = sampleRequest();

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Flight.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookingService.createBooking(req))
                .expectErrorSatisfies(err -> {
                    assert err instanceof RuntimeException;
                    assert err.getMessage().contains("Not enough seats");
                }).verify();
    }
    
    @Test
    void createBooking_duplicateKey_retriesAndSucceeds() {

        // ---- Build a VALID BookingRequest ----
        PassengerDTO p = new PassengerDTO();
        p.setName("Alice");
        p.setAge(25);

        BookingRequest req = new BookingRequest();
        req.setFlightId("F1");
        req.setSeatNumbers(List.of("1A", "1B"));
        req.setAmountPaid(500.0);
        req.setEmailId("test@example.com");
        req.setPassengers(List.of(p));

        // ---- Mock Flight update (seat decrement) ----
        Flight updatedFlight = new Flight();
        updatedFlight.setId("F1");

        when(mongoTemplate.findAndModify(any(), any(), any(), eq(Flight.class)))
                .thenReturn(Mono.just(updatedFlight));

        // ---- PNR generator must return DIFFERENT PNRs ----
        when(pnrGenerator.generate())
                .thenReturn("PNR1")   // first attempt → collision
                .thenReturn("PNR2");  // retry → success

        // ---- Mock final successful booking save ----
        Booking saved = Booking.builder()
                .pnr("PNR2")  // must match retry PNR
                .flightId("F1")
                .seatNumbers(List.of("1A", "1B"))
                .amountPaid(500.0)
                .emailId("test@example.com")
                .status(BookingStatus.CONFIRMED)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .passengers(List.of(
                        Passenger.builder().name("Alice").age(25).build()
                ))
                .build();

        // ---- FIRST save throws DuplicateKey → SECOND save succeeds ----
        when(bookingRepo.save(any()))
                .thenReturn(Mono.error(new DuplicateKeyException("dup")))
                .thenReturn(Mono.just(saved));

        // ---- VERIFY ----
        StepVerifier.create(bookingService.createBooking(req))
                .expectNext("PNR2")
                .verifyComplete();

        verify(bookingRepo, times(2)).save(any());
    }



    @Test
    void getBooking_found_returnsResponse() {
        Booking b = new Booking();
        b.setPnr("P1");
        b.setFlightId("flight-1");
        List<String> seats = new ArrayList<>();
        seats.add("1A");
        b.setSeatNumbers(seats);
        b.setAmountPaid(100.0);
        b.setEmailId("e@x.com");
        b.setStatus(BookingStatus.CONFIRMED);

        Passenger passenger = new Passenger();
        passenger.setName("Alice");
        passenger.setAge(30);
        b.setPassengers(List.of(passenger));
        b.setCreatedAt(Instant.now());

        when(bookingRepo.findByPnr("P1")).thenReturn(Mono.just(b));

        StepVerifier.create(bookingService.getBooking("P1"))
                .assertNext(resp -> {
                    assert resp.getPnr().equals("P1");
                    assert resp.getEmailId().equals("e@x.com");
                    assert resp.getSeatNumbers().contains("1A");
                })
                .verifyComplete();
    }

    @Test
    void getBooking_missing_throwsNotFound() {
        when(bookingRepo.findByPnr("X")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getBooking("X"))
                .expectErrorSatisfies(err -> {
                    assert err instanceof NotFoundException;
                    assert err.getMessage().contains("Booking not found");
                }).verify();
    }

    @Test
    void cancelBooking_restoresSeats_andCompletes() {
        Booking b = new Booking();
        b.setPnr("C1");
        b.setFlightId("flight-1");
        b.setSeatNumbers(List.of("1A", "1B"));
        b.setStatus(BookingStatus.CONFIRMED);

        when(bookingRepo.findByPnr("C1")).thenReturn(Mono.just(b));
        when(bookingRepo.save(any(Booking.class))).thenReturn(Mono.just(b));

        Flight newFlight = new Flight();
        newFlight.setId("flight-1");
        newFlight.setAvailableSeats(100);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Flight.class)))
                .thenReturn(Mono.just(newFlight));

        StepVerifier.create(bookingService.cancelBooking("C1"))
                .verifyComplete();

        verify(bookingRepo, times(1)).save(any(Booking.class));
        verify(mongoTemplate, times(1)).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Flight.class));
    }

    @Test
    void cancelBooking_alreadyCancelled_returnsEmpty() {
        Booking b = new Booking();
        b.setPnr("C2");
        b.setFlightId("flight-1");
        b.setSeatNumbers(List.of("1A"));
        b.setStatus(BookingStatus.CANCELLED);

        when(bookingRepo.findByPnr("C2")).thenReturn(Mono.just(b));

        StepVerifier.create(bookingService.cancelBooking("C2"))
                .verifyComplete();

        verify(bookingRepo, never()).save(any());
        verify(mongoTemplate, never()).findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(Flight.class));
    }

    @Test
    void cancelBooking_missing_throwsNotFound() {
        when(bookingRepo.findByPnr("Z")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.cancelBooking("Z"))
                .expectErrorSatisfies(err -> {
                    assert err instanceof NotFoundException;
                }).verify();
    }
}
