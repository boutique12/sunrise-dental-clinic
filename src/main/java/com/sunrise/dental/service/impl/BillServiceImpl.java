package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.BillDao;
import com.sunrise.dental.dao.TreatmentDao;
import com.sunrise.dental.dao.TreatmentDetailDao;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.model.TreatmentDetail;
import com.sunrise.dental.service.BillService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.NumberGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BillServiceImpl implements BillService {

    private final BillDao billDao;
    private final TreatmentDao treatmentDao;
    private final TreatmentDetailDao treatmentDetailDao;

    public BillServiceImpl(BillDao billDao, TreatmentDao treatmentDao, TreatmentDetailDao treatmentDetailDao) {
        this.billDao = billDao;
        this.treatmentDao = treatmentDao;
        this.treatmentDetailDao = treatmentDetailDao;
    }

    @Override
    public List<Bill> getAllBills() {
        return billDao.findAll();
    }

    @Override
    public Optional<Bill> getBillById(Long billId) {
        return billDao.findById(billId);
    }

    @Override
    public Optional<Bill> getBillByAppointmentId(Long appointmentId) {
        return billDao.findByAppointmentId(appointmentId);
    }

    @Override
    public Long generateBillForAppointment(Long appointmentId, BigDecimal discount) {
        if (appointmentId == null) {
            throw new ValidationException("Appointment id is required");
        }
        if (billDao.findByAppointmentId(appointmentId).isPresent()) {
            throw new ValidationException("Bill already exists for this appointment");
        }

        Treatment treatment = treatmentDao.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ValidationException("Treatment must be recorded before generating a bill"));

        List<TreatmentDetail> details = treatmentDetailDao.findByTreatmentId(treatment.getTreatmentId());
        if (details.isEmpty()) {
            throw new ValidationException("Treatment has no chargeable items");
        }

        BigDecimal subtotal = details.stream()
                .map(TreatmentDetail::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal safeDiscount = discount != null ? discount : BigDecimal.ZERO;
        if (safeDiscount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Discount cannot be negative");
        }
        if (safeDiscount.compareTo(subtotal) > 0) {
            throw new ValidationException("Discount cannot exceed subtotal");
        }

        Bill bill = new Bill();
        bill.setAppointmentId(appointmentId);
        String latest = billDao.findLatestBillNumber();
        int sequence = NumberGenerator.extractSequence(latest, "BIL");
        bill.setBillNumber(NumberGenerator.generateBillNumber(sequence));
        bill.setSubtotal(subtotal);
        bill.setDiscount(safeDiscount);
        bill.setTotalAmount(subtotal.subtract(safeDiscount));
        bill.setPaymentStatus(AppConstants.PAYMENT_UNPAID);
        return billDao.insert(bill);
    }

    @Override
    public void updateBill(Bill bill) {
        if (bill == null || bill.getBillId() == null) {
            throw new ValidationException("Bill id is required for update");
        }
        if (bill.getDiscount() == null || bill.getSubtotal() == null) {
            throw new ValidationException("Subtotal and discount are required");
        }
        if (bill.getDiscount().compareTo(bill.getSubtotal()) > 0) {
            throw new ValidationException("Discount cannot exceed subtotal");
        }
        bill.setTotalAmount(bill.getSubtotal().subtract(bill.getDiscount()));
        billDao.update(bill);
    }
}
