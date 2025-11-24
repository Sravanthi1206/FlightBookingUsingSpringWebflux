package com.flightapp.repository;

import com.flightapp.model.PaymentRecord;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PaymentRecordRepository extends ReactiveMongoRepository<PaymentRecord, String> {
}
