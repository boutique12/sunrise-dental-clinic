package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.ReportDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.ReportSummary;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportDaoJdbc implements ReportDao {

    private final DatabaseConnection databaseConnection;

    public ReportDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public ReportSummary generateDailySummary(LocalDate date) {
        ReportSummary summary = new ReportSummary();
        summary.setReportDate(date);

        try (Connection conn = databaseConnection.getConnection()) {
            summary.setTotalAppointments(countAppointments(conn, date, null));
            summary.setCompletedAppointments(countAppointments(conn, date, AppConstants.STATUS_COMPLETED));
            summary.setCancelledAppointments(countAppointments(conn, date, AppConstants.STATUS_CANCELLED));
            summary.setNewPatients(countNewPatients(conn, date));
            summary.setTreatmentsPerformed(countTreatments(conn, date));
            summary.setTotalRevenue(sumBillTotals(conn, date));
            summary.setTotalCollected(sumPayments(conn, date));
            summary.setUnpaidBills(countUnpaidBills(conn));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to generate daily report summary", e);
        }

        return summary;
    }

    private int countAppointments(Connection conn, LocalDate date, String status) throws SQLException {
        String sql = status == null
                ? "SELECT COUNT(*) FROM appointments WHERE appointment_date = ?"
                : "SELECT COUNT(*) FROM appointments WHERE appointment_date = ? AND status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            if (status != null) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int countNewPatients(Connection conn, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM patients WHERE DATE(created_at) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private int countTreatments(Connection conn, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM treatments WHERE treatment_date = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private BigDecimal sumBillTotals(Connection conn, LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bills WHERE DATE(created_at) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    private BigDecimal sumPayments(Connection conn, LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE(payment_date) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    private int countUnpaidBills(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM bills WHERE payment_status <> 'PAID'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
