package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.PaymentDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.Payment;
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

public class PaymentDaoJdbc implements PaymentDao {

    private static final String BASE_SELECT = """
            SELECT p.*, b.bill_number,
                   CONCAT(pt.first_name, ' ', pt.last_name) AS patient_name,
                   u.full_name AS received_by_name
            FROM payments p
            JOIN bills b ON p.bill_id = b.bill_id
            JOIN appointments a ON b.appointment_id = a.appointment_id
            JOIN patients pt ON a.patient_id = pt.patient_id
            JOIN users u ON p.received_by = u.user_id
            """;

    private final DatabaseConnection databaseConnection;

    public PaymentDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Payment> findAll() {
        return queryPayments(BASE_SELECT + " ORDER BY p.payment_date DESC");
    }

    @Override
    public Optional<Payment> findById(Long paymentId) {
        String sql = BASE_SELECT + " WHERE p.payment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find payment by id", e);
        }
    }

    @Override
    public List<Payment> findByBillId(Long billId) {
        String sql = BASE_SELECT + " WHERE p.bill_id = ? ORDER BY p.payment_date DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find payments by bill", e);
        }
    }

    @Override
    public String findLatestPaymentNumber() {
        String sql = """
                SELECT payment_number FROM payments
                WHERE payment_number LIKE ?
                ORDER BY payment_id DESC LIMIT 1
                """;
        String pattern = "PAY-" + LocalDate.now().getYear() + "-%";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("payment_number");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find latest payment number", e);
        }
    }

    @Override
    public Long insert(Payment payment) {
        String sql = """
                INSERT INTO payments (payment_number, bill_id, amount, payment_method,
                    payment_date, received_by, reference_number, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, payment.getPaymentNumber());
            ps.setLong(2, payment.getBillId());
            ps.setBigDecimal(3, payment.getAmount());
            ps.setString(4, payment.getPaymentMethod());
            if (payment.getPaymentDate() != null) {
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(payment.getPaymentDate()));
            } else {
                ps.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
            }
            ps.setLong(6, payment.getReceivedBy());
            ps.setString(7, payment.getReferenceNumber());
            ps.setString(8, payment.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new DatabaseException("Failed to retrieve generated payment id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert payment", e);
        }
    }

    @Override
    public BigDecimal sumByBillId(Long billId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE bill_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to sum payments by bill", e);
        }
    }

    @Override
    public BigDecimal sumByDate(LocalDate date) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payments WHERE DATE(payment_date) = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to sum payments by date", e);
        }
    }

    private List<Payment> queryPayments(String sql) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query payments", e);
        }
    }

    private List<Payment> mapList(ResultSet rs) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        while (rs.next()) {
            payments.add(mapRow(rs));
        }
        return payments;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getLong("payment_id"));
        payment.setPaymentNumber(rs.getString("payment_number"));
        payment.setBillId(rs.getLong("bill_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentDate(JdbcMapper.toLocalDateTime(rs.getTimestamp("payment_date")));
        payment.setReceivedBy(rs.getLong("received_by"));
        payment.setReferenceNumber(rs.getString("reference_number"));
        payment.setNotes(rs.getString("notes"));
        payment.setBillNumber(rs.getString("bill_number"));
        payment.setPatientName(rs.getString("patient_name"));
        payment.setReceivedByName(rs.getString("received_by_name"));
        return payment;
    }
}
