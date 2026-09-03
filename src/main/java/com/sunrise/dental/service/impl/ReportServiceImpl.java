package com.sunrise.dental.service.impl;

import com.sunrise.dental.dao.AppointmentDao;
import com.sunrise.dental.dao.BillDao;
import com.sunrise.dental.dao.PatientDao;
import com.sunrise.dental.dao.PaymentDao;
import com.sunrise.dental.dao.ReportDao;
import com.sunrise.dental.dao.TreatmentDao;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.Payment;
import com.sunrise.dental.model.ReportResult;
import com.sunrise.dental.model.ReportSummary;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.service.ReportService;
import com.sunrise.dental.util.AppConstants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportServiceImpl implements ReportService {

    private final ReportDao reportDao;
    private final AppointmentDao appointmentDao;
    private final BillDao billDao;
    private final PatientDao patientDao;
    private final TreatmentDao treatmentDao;
    private final PaymentDao paymentDao;

    public ReportServiceImpl(ReportDao reportDao, AppointmentDao appointmentDao, BillDao billDao,
                             PatientDao patientDao, TreatmentDao treatmentDao, PaymentDao paymentDao) {
        this.reportDao = reportDao;
        this.appointmentDao = appointmentDao;
        this.billDao = billDao;
        this.patientDao = patientDao;
        this.treatmentDao = treatmentDao;
        this.paymentDao = paymentDao;
    }

    @Override
    public ReportSummary getDailySummary(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return reportDao.generateDailySummary(date);
    }

    @Override
    public ReportResult generateAppointmentsReport(LocalDate fromDate, LocalDate toDate) {
        List<String> headers = List.of("Appt #", "Date", "Time", "Patient", "Dentist", "Reason", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (Appointment appt : appointmentDao.findAll()) {
            if (!isWithinRange(appt.getAppointmentDate(), fromDate, toDate)) {
                continue;
            }
            rows.add(List.of(
                    safe(appt.getAppointmentNumber()),
                    safe(appt.getAppointmentDate()),
                    safe(appt.getAppointmentTime()),
                    safe(appt.getPatientName()),
                    safe(appt.getDentistName()),
                    safe(appt.getReason()),
                    safe(appt.getStatus())
            ));
        }
        String title = "Appointment Report (" + fromDate + " to " + toDate + ")";
        return new ReportResult(title, headers, rows);
    }

    @Override
    public ReportResult generateRevenueReport(LocalDate fromDate, LocalDate toDate) {
        List<String> headers = List.of("Bill #", "Date", "Patient", "Subtotal", "Discount", "Total", "Status");
        List<List<String>> rows = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Bill bill : billDao.findAll()) {
            if (!isWithinRange(bill.getCreatedAt(), fromDate, toDate)) {
                continue;
            }
            rows.add(List.of(
                    safe(bill.getBillNumber()),
                    safe(bill.getCreatedAt() != null ? bill.getCreatedAt().toLocalDate() : null),
                    safe(bill.getPatientName()),
                    formatMoney(bill.getSubtotal()),
                    formatMoney(bill.getDiscount()),
                    formatMoney(bill.getTotalAmount()),
                    safe(bill.getPaymentStatus())
            ));
            if (bill.getTotalAmount() != null) {
                grandTotal = grandTotal.add(bill.getTotalAmount());
            }
        }
        rows.add(List.of("", "", "Grand Total", "", "", formatMoney(grandTotal), ""));
        String title = "Revenue Report (" + fromDate + " to " + toDate + ")";
        return new ReportResult(title, headers, rows);
    }

    @Override
    public ReportResult generatePatientsReport(LocalDate fromDate, LocalDate toDate) {
        List<String> headers = List.of("Patient #", "Name", "Date of Birth", "Gender", "Phone", "Registered", "Status");
        List<List<String>> rows = new ArrayList<>();
        for (Patient patient : patientDao.findAll()) {
            if (fromDate != null || toDate != null) {
                if (!isWithinRange(patient.getCreatedAt(), fromDate, toDate)) {
                    continue;
                }
            }
            rows.add(List.of(
                    safe(patient.getPatientNumber()),
                    safe(patient.getFullName()),
                    safe(patient.getDateOfBirth()),
                    safe(patient.getGender()),
                    safe(patient.getPhone()),
                    safe(patient.getCreatedAt() != null ? patient.getCreatedAt().toLocalDate() : null),
                    patient.isActive() ? "Active" : "Inactive"
            ));
        }
        String range = formatRange(fromDate, toDate);
        return new ReportResult("Patient Report" + range, headers, rows);
    }

    @Override
    public ReportResult generateTreatmentsReport(LocalDate fromDate, LocalDate toDate) {
        List<String> headers = List.of("Date", "Patient", "Appointment #", "Dentist", "Diagnosis");
        List<List<String>> rows = new ArrayList<>();
        for (Treatment treatment : treatmentDao.findAll()) {
            if (!isWithinRange(treatment.getTreatmentDate(), fromDate, toDate)) {
                continue;
            }
            rows.add(List.of(
                    safe(treatment.getTreatmentDate()),
                    safe(treatment.getPatientName()),
                    safe(treatment.getAppointmentNumber()),
                    safe(treatment.getDentistName()),
                    safe(treatment.getDiagnosis())
            ));
        }
        String title = "Treatment Report (" + fromDate + " to " + toDate + ")";
        return new ReportResult(title, headers, rows);
    }

    @Override
    public ReportResult generatePaymentsReport(LocalDate fromDate, LocalDate toDate) {
        List<String> headers = List.of("Payment #", "Date", "Bill #", "Patient", "Amount", "Method", "Received By");
        List<List<String>> rows = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (Payment payment : paymentDao.findAll()) {
            if (!isWithinRange(payment.getPaymentDate(), fromDate, toDate)) {
                continue;
            }
            rows.add(List.of(
                    safe(payment.getPaymentNumber()),
                    safe(payment.getPaymentDate()),
                    safe(payment.getBillNumber()),
                    safe(payment.getPatientName()),
                    formatMoney(payment.getAmount()),
                    safe(payment.getPaymentMethod()),
                    safe(payment.getReceivedByName())
            ));
            if (payment.getAmount() != null) {
                grandTotal = grandTotal.add(payment.getAmount());
            }
        }
        rows.add(List.of("", "", "", "Total Collected", formatMoney(grandTotal), "", ""));
        String title = "Payment Summary (" + fromDate + " to " + toDate + ")";
        return new ReportResult(title, headers, rows);
    }

    @Override
    public ReportResult generateOutstandingBillsReport() {
        List<String> headers = List.of("Bill #", "Patient", "Appointment #", "Total", "Paid", "Outstanding", "Status");
        List<List<String>> rows = new ArrayList<>();
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        for (Bill bill : billDao.findAll()) {
            if (AppConstants.PAYMENT_PAID.equals(bill.getPaymentStatus())) {
                continue;
            }
            BigDecimal outstanding = bill.getOutstandingAmount();
            rows.add(List.of(
                    safe(bill.getBillNumber()),
                    safe(bill.getPatientName()),
                    safe(bill.getAppointmentNumber()),
                    formatMoney(bill.getTotalAmount()),
                    formatMoney(bill.getAmountPaid()),
                    formatMoney(outstanding),
                    safe(bill.getPaymentStatus())
            ));
            totalOutstanding = totalOutstanding.add(outstanding);
        }
        rows.add(List.of("", "", "", "", "Total Outstanding", formatMoney(totalOutstanding), ""));
        return new ReportResult("Outstanding Bills Report", headers, rows);
    }

    private boolean isWithinRange(LocalDate value, LocalDate fromDate, LocalDate toDate) {
        if (value == null) {
            return false;
        }
        if (fromDate != null && value.isBefore(fromDate)) {
            return false;
        }
        return toDate == null || !value.isAfter(toDate);
    }

    private boolean isWithinRange(LocalDateTime value, LocalDate fromDate, LocalDate toDate) {
        if (value == null) {
            return false;
        }
        return isWithinRange(value.toLocalDate(), fromDate, toDate);
    }

    private String formatRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null && toDate == null) {
            return "";
        }
        if (fromDate != null && toDate != null) {
            return " (" + fromDate + " to " + toDate + ")";
        }
        if (fromDate != null) {
            return " (from " + fromDate + ")";
        }
        return " (to " + toDate + ")";
    }

    private String safe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String formatMoney(BigDecimal amount) {
        return amount == null ? "0.00" : amount.toPlainString();
    }
}
