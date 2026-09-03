package com.sunrise.dental.dao;

import com.sunrise.dental.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentDao {

    List<Payment> findAll();

    Optional<Payment> findById(Long paymentId);

    List<Payment> findByBillId(Long billId);

    String findLatestPaymentNumber();

    Long insert(Payment payment);

    BigDecimal sumByBillId(Long billId);

    BigDecimal sumByDate(LocalDate date);
}
