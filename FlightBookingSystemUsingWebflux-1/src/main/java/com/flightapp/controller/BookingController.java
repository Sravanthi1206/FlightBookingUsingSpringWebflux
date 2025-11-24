package com.flightapp.controller;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.exception.NotFoundException;
import com.flightapp.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/flight/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Mono<ResponseEntity<String>> createBooking(@Valid @RequestBody BookingRequest req) {
        return bookingService.createBooking(req)
                .map(pnr -> ResponseEntity.status(HttpStatus.CREATED).body(pnr));
    }


    @GetMapping("/{pnr}")
    public Mono<ResponseEntity<BookingResponse>> getBooking(@PathVariable String pnr) {
        return bookingService.getBooking(pnr)
                .map(ResponseEntity::ok)
                .onErrorResume(NotFoundException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/history")
    public Flux<BookingResponse> bookingHistory(@RequestParam("email") String email) {
        return bookingService.getBookingsByEmail(email);
    }

    @DeleteMapping("/{pnr}")
    public Mono<ResponseEntity<Void>> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(NotFoundException.class, e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
