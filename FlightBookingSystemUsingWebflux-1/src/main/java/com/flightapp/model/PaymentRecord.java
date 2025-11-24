package com.flightapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.flightapp.model.enums.PaymentStatus;

import jakarta.validation.constraints.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "payments")
public class PaymentRecord {

    @Id
    private String id;

    @NotBlank
    private String bookingPnr;

    @NotBlank
    private String paymentProvider;

    @NotNull
    @PositiveOrZero
    private Double amount;

    private String currency;

    private Instant paidAt;

    private String transactionId;

    private PaymentStatus status;
}
