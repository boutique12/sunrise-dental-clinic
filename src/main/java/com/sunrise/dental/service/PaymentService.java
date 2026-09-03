package com.sunrise.dental.service;

import com.sunrise.dental.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<Payment> getAllPayments();

    Optional<Payment> getPaymentById(Long paymentId);

    List<Payment> getPaymentsByBillId(Long billId);

    Long recordPayment(Payment payment);
}
