package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.dto.PassengerDTO;
import com.flightapp.exception.NotFoundException;
import com.flightapp.model.Booking;
import com.flightapp.model.Flight;
import com.flightapp.model.Passenger;
import com.flightapp.model.enums.BookingStatus;
import com.flightapp.repository.BookingRepository;
import com.flightapp.util.PnrGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepo;
    private final ReactiveMongoTemplate mongoTemplate;
    private final PnrGenerator pnrGenerator;
    
    private static final String FIELD_AVAILABLE_SEATS = "availableSeats";

    public Mono<String> createBooking(BookingRequest req) {
    int seatsRequested = req.getSeatNumbers().size();

    Query q = Query.query(Criteria.where("_id").is(req.getFlightId())
            .and(FIELD_AVAILABLE_SEATS).gte(seatsRequested));
    Update u = new Update().inc(FIELD_AVAILABLE_SEATS, -seatsRequested);

    return mongoTemplate.findAndModify(q, u, FindAndModifyOptions.options().returnNew(true), Flight.class)
            .switchIfEmpty(Mono.error(new RuntimeException("Not enough seats available")))
            .flatMap(updated -> {
                Booking b = Booking.builder()
                        .pnr(pnrGenerator.generate())
                        .flightId(req.getFlightId())
                        .seatNumbers(req.getSeatNumbers())
                        .amountPaid(req.getAmountPaid())
                        .emailId(req.getEmailId())
                        .status(BookingStatus.CONFIRMED)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .passengers(req.getPassengers().stream()
                                .map(this::toModelPassenger)
                                .collect(Collectors.toList()))
                        .build();

                return bookingRepo.save(b)
                        .onErrorResume(DuplicateKeyException.class, ex -> {
                            b.setPnr(pnrGenerator.generate());
                            return bookingRepo.save(b);
                        })
                        .map(saved -> saved.getPnr()); 
            });
}


    public Mono<BookingResponse> getBooking(String pnr) {
        return bookingRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found")))
                .map(this::toResponse);
    }

    public Flux<BookingResponse> getBookingsByEmail(String email) {
        return bookingRepo.findByEmailIdOrderByCreatedAtDesc(email)
                .map(this::toResponse);
    }

    public Mono<Void> cancelBooking(String pnr) {
        return bookingRepo.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new NotFoundException("Booking not found")))
                .flatMap(b -> {
                    if (b.getStatus() == BookingStatus.CANCELLED) {
                        return Mono.empty();
                    }
                    b.setStatus(BookingStatus.CANCELLED);
                    b.setUpdatedAt(Instant.now());

                    int seatsToRestore = b.getSeatNumbers().size();

                    return bookingRepo.save(b)
                            .then(mongoTemplate.findAndModify(
                                    Query.query(Criteria.where("_id").is(b.getFlightId())),
                                    new Update().inc(FIELD_AVAILABLE_SEATS, seatsToRestore),
                                    FindAndModifyOptions.options().returnNew(true),
                                    Flight.class
                            ).then());
                }).then();
    }

    private Passenger toModelPassenger(PassengerDTO dto) {
        return Passenger.builder()
                .name(dto.getName())
                .age(dto.getAge())
                .gender(null)
                .passportNumber(null)
                .build();
    }

    private BookingResponse toResponse(Booking b) {
        List<PassengerDTO> passengers = b.getPassengers().stream()
                .map(p -> {
                    PassengerDTO dto = new PassengerDTO();
                    dto.setName(p.getName());
                    dto.setAge(p.getAge());
                    return dto;
                }).collect(Collectors.toList());

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
}

