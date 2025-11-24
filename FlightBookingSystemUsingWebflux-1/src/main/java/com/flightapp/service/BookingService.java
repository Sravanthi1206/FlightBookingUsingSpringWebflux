package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.dto.Passenger;
import com.flightapp.model.Booking;
import com.flightapp.model.enums.BookingStatus;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final FlightRepository flightRepo;

    public Mono<BookingResponse> createBooking(BookingRequest req) {
        return flightRepo.findById(req.getFlightId())
                .switchIfEmpty(Mono.error(new RuntimeException("Flight not found")))
                .flatMap(flight -> {

                    Booking b = Booking.builder()
                            .pnr(generatePnr())
                            .flightId(req.getFlightId())
                            .seatNumbers(req.getSeatNumbers())
                            .amountPaid(req.getAmountPaid())
                            .emailId(req.getEmailId())
                            .status(BookingStatus.CONFIRMED)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .passengers(req.getPassengers().stream()
                                    .map(p -> com.flightapp.model.Passenger.builder()
                                            .name(p.getName())
                                            .age(p.getAge())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build();

                    return bookingRepo.save(b).map(this::toResponse);
                });
    }

    public Mono<BookingResponse> getBooking(String pnr) {
        return bookingRepo.findByPnr(pnr).map(this::toResponse);
    }

    public Flux<BookingResponse> getBookingsByEmail(String emailId) {
        return bookingRepo.findByEmailIdOrderByCreatedAtDesc(emailId)
                .map(this::toResponse);
    }

    private BookingResponse toResponse(Booking b) {
        List<Passenger> passengers = b.getPassengers().stream()
                .map(p -> {
                    Passenger temp = new Passenger();
                    temp.setName(p.getName());
                    temp.setAge(p.getAge());
                    return temp;
                })
                .collect(Collectors.toList());

        return BookingResponse.builder()
                .pnr(b.getPnr())
                .flightId(b.getFlightId())
                .seatNumbers(b.getSeatNumbers())
                .amountPaid(b.getAmountPaid())
                .emailId(b.getEmailId())
                .status(b.getStatus().name())
                .passengers(passengers)
                .createdAt(b.getCreatedAt())
                .build();
    }

    private String generatePnr() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
