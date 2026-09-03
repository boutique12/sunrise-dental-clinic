package com.sunrise.dental.service;

import com.sunrise.dental.model.ReportResult;
import com.sunrise.dental.model.ReportSummary;

import java.time.LocalDate;

public interface ReportService {

    ReportSummary getDailySummary(LocalDate date);

    ReportResult generateAppointmentsReport(LocalDate fromDate, LocalDate toDate);

    ReportResult generateRevenueReport(LocalDate fromDate, LocalDate toDate);

    ReportResult generatePatientsReport(LocalDate fromDate, LocalDate toDate);

    ReportResult generateTreatmentsReport(LocalDate fromDate, LocalDate toDate);

    ReportResult generatePaymentsReport(LocalDate fromDate, LocalDate toDate);

    ReportResult generateOutstandingBillsReport();
}
