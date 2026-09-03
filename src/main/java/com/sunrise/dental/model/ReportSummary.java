package com.sunrise.dental.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportSummary {

    private LocalDate reportDate;
    private int totalAppointments;
    private int completedAppointments;
    private int cancelledAppointments;
    private int newPatients;
    private int treatmentsPerformed;
    private BigDecimal totalRevenue;
    private BigDecimal totalCollected;
    private int unpaidBills;

    public ReportSummary() {
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(int completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public int getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(int cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public int getNewPatients() {
        return newPatients;
    }

    public void setNewPatients(int newPatients) {
        this.newPatients = newPatients;
    }

    public int getTreatmentsPerformed() {
        return treatmentsPerformed;
    }

    public void setTreatmentsPerformed(int treatmentsPerformed) {
        this.treatmentsPerformed = treatmentsPerformed;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public int getUnpaidBills() {
        return unpaidBills;
    }

    public void setUnpaidBills(int unpaidBills) {
        this.unpaidBills = unpaidBills;
    }
}
