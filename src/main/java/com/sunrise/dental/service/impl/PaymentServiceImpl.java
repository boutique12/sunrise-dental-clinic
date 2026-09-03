package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.BillDao;
import com.sunrise.dental.dao.PaymentDao;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Payment;
import com.sunrise.dental.service.PaymentService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.NumberGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDao paymentDao;
    private final BillDao billDao;

    public PaymentServiceImpl(PaymentDao paymentDao, BillDao billDao) {
        this.paymentDao = paymentDao;
        this.billDao = billDao;
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDao.findAll();
    }

    @Override
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentDao.findById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsByBillId(Long billId) {
        return paymentDao.findByBillId(billId);
    }

    @Override
    public Long recordPayment(Payment payment) {
        validatePayment(payment);

        Bill bill = billDao.findById(payment.getBillId())
                .orElseThrow(() -> new ValidationException("Bill not found"));

        BigDecimal alreadyPaid = paymentDao.sumByBillId(payment.getBillId());
        BigDecimal balance = bill.getTotalAmount().subtract(alreadyPaid);
        if (payment.getAmount().compareTo(balance) > 0) {
            throw new ValidationException("Payment amount exceeds outstanding balance");
        }

        String latest = paymentDao.findLatestPaymentNumber();
        int sequence = NumberGenerator.extractSequence(latest, "PAY");
        payment.setPaymentNumber(NumberGenerator.generatePaymentNumber(sequence));
        Long paymentId = paymentDao.insert(payment);

        BigDecimal totalPaid = alreadyPaid.add(payment.getAmount());
        String status;
        if (totalPaid.compareTo(bill.getTotalAmount()) >= 0) {
            status = AppConstants.PAYMENT_PAID;
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            status = AppConstants.PAYMENT_PARTIALLY_PAID;
        } else {
            status = AppConstants.PAYMENT_UNPAID;
        }
        billDao.updatePaymentStatus(payment.getBillId(), status);

        return paymentId;
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new ValidationException("Payment data is required");
        }
        if (payment.getBillId() == null) {
            throw new ValidationException("Bill is required");
        }
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than zero");
        }
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().isBlank()) {
            throw new ValidationException("Payment method is required");
        }
        if (payment.getReceivedBy() == null) {
            throw new ValidationException("Receiving staff member is required");
        }
    }
}
