package com.flightapp.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        NotFoundException ex = new NotFoundException("Not found");

        Mono<ResponseEntity<ErrorResponse>> result = handler.handleNotFound(ex);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.getStatusCode() == HttpStatus.NOT_FOUND;
                    assert resp.getBody().getMessage().equals("Not found");
                })
                .verifyComplete();
    }

    @Test
    void handleBadRequest_returns400() {
        BadRequestException ex = new BadRequestException("Bad input");

        Mono<ResponseEntity<ErrorResponse>> result = handler.handleBadRequest(ex);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.getStatusCode() == HttpStatus.BAD_REQUEST;
                    assert resp.getBody().getMessage().equals("Bad input");
                })
                .verifyComplete();
    }

    @Test
    void handleValidation_returns400_withDetails() {
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "object");
        binding.addError(new FieldError("object", "age", "must be >= 18"));
        WebExchangeBindException ex = new WebExchangeBindException(null, binding);

        Mono<ResponseEntity<ErrorResponse>> result = handler.handleValidation(ex);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.getStatusCode() == HttpStatus.BAD_REQUEST;
                    assert resp.getBody().getDetails().get(0).contains("age");
                })
                .verifyComplete();
    }

    @Test
    void handleServerWebInput_returns400() {
        ServerWebInputException ex = new ServerWebInputException("Invalid body");

        Mono<ResponseEntity<ErrorResponse>> result = handler.handleServerWebInput(ex);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.getStatusCode() == HttpStatus.BAD_REQUEST;
                    assert resp.getBody().getMessage().equals("Invalid body");
                })
                .verifyComplete();
    }

    @Test
    void handleGeneric_returns500() {
        Exception ex = new Exception("Something went wrong");

        Mono<ResponseEntity<ErrorResponse>> result = handler.handleGeneric(ex);

        StepVerifier.create(result)
                .assertNext(resp -> {
                    assert resp.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
                    assert resp.getBody().getMessage().equals("An unexpected error occurred");
                })
                .verifyComplete();
    }
}
