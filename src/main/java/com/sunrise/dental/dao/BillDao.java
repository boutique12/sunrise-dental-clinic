package com.sunrise.dental.dao;

import com.sunrise.dental.model.Bill;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillDao {

    List<Bill> findAll();

    Optional<Bill> findById(Long billId);

    Optional<Bill> findByAppointmentId(Long appointmentId);

    String findLatestBillNumber();

    Long insert(Bill bill);

    void update(Bill bill);

    void updatePaymentStatus(Long billId, String status);

    int countUnpaid();

    BigDecimal sumTotalAmountByDate(java.time.LocalDate date);
}
