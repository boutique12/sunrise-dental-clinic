package com.sunrise.dental.service;

import com.sunrise.dental.model.Bill;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BillService {

    List<Bill> getAllBills();

    Optional<Bill> getBillById(Long billId);

    Optional<Bill> getBillByAppointmentId(Long appointmentId);

    Long generateBillForAppointment(Long appointmentId, BigDecimal discount);

    void updateBill(Bill bill);
}
