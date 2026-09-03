package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.BillDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BillDaoJdbc implements BillDao {

    private static final String BASE_SELECT = """
            SELECT b.*,
                   CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
                   p.patient_number,
                   a.appointment_number,
                   COALESCE((SELECT SUM(amount) FROM payments WHERE bill_id = b.bill_id), 0) AS amount_paid
            FROM bills b
            JOIN appointments a ON b.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            """;

    private final DatabaseConnection databaseConnection;

    public BillDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Bill> findAll() {
        return queryBills(BASE_SELECT + " ORDER BY b.created_at DESC");
    }

    @Override
    public Optional<Bill> findById(Long billId) {
        String sql = BASE_SELECT + " WHERE b.bill_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find bill by id", e);
        }
    }

    @Override
    public Optional<Bill> findByAppointmentId(Long appointmentId) {
        String sql = BASE_SELECT + " WHERE b.appointment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find bill by appointment", e);
        }
    }

    @Override
    public String findLatestBillNumber() {
        String sql = """
                SELECT bill_number FROM bills
                WHERE bill_number LIKE ?
                ORDER BY bill_id DESC LIMIT 1
                """;
        String pattern = "BIL-" + LocalDate.now().getYear() + "-%";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("bill_number");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find latest bill number", e);
        }
    }

    @Override
    public Long insert(Bill bill) {
        String sql = """
                INSERT INTO bills (bill_number, appointment_id, subtotal, discount, total_amount, payment_status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bill.getBillNumber());
            ps.setLong(2, bill.getAppointmentId());
            ps.setBigDecimal(3, bill.getSubtotal());
            ps.setBigDecimal(4, bill.getDiscount());
            ps.setBigDecimal(5, bill.getTotalAmount());
            ps.setString(6, bill.getPaymentStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new DatabaseException("Failed to retrieve generated bill id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert bill", e);
        }
    }

    @Override
    public void update(Bill bill) {
        String sql = """
                UPDATE bills SET subtotal = ?, discount = ?, total_amount = ?, payment_status = ?
                WHERE bill_id = ?
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, bill.getSubtotal());
            ps.setBigDecimal(2, bill.getDiscount());
            ps.setBigDecimal(3, bill.getTotalAmount());
            ps.setString(4, bill.getPaymentStatus());
            ps.setLong(5, bill.getBillId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update bill", e);
        }
    }

    @Override
    public void updatePaymentStatus(Long billId, String status) {
        String sql = "UPDATE bills SET payment_status = ? WHERE bill_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, billId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update bill payment status", e);
        }
    }

    @Override
    public int countUnpaid() {
        String sql = "SELECT COUNT(*) FROM bills WHERE payment_status <> 'PAID'";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count unpaid bills", e);
        }
    }

    @Override
    public BigDecimal sumTotalAmountByDate(LocalDate date) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM bills WHERE DATE(created_at) = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to sum bill totals by date", e);
        }
    }

    private List<Bill> queryBills(String sql) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Bill> bills = new ArrayList<>();
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
            return bills;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query bills", e);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getLong("bill_id"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setAppointmentId(rs.getLong("appointment_id"));
        bill.setSubtotal(rs.getBigDecimal("subtotal"));
        bill.setDiscount(rs.getBigDecimal("discount"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setCreatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("created_at")));
        bill.setUpdatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("updated_at")));
        bill.setPatientName(rs.getString("patient_name"));
        bill.setPatientNumber(rs.getString("patient_number"));
        bill.setAppointmentNumber(rs.getString("appointment_number"));
        bill.setAmountPaid(rs.getBigDecimal("amount_paid"));
        return bill;
    }
}
