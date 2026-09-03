package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.AppointmentDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDaoJdbc implements AppointmentDao {

    private static final String BASE_SELECT = """
            SELECT a.*,
                   CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
                   u.full_name AS dentist_name
            FROM appointments a
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN users u ON a.dentist_id = u.user_id
            """;

    private final DatabaseConnection databaseConnection;

    public AppointmentDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Appointment> findAll() {
        return queryAppointments(BASE_SELECT + " ORDER BY a.appointment_date DESC, a.appointment_time DESC");
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        String sql = BASE_SELECT + " WHERE a.appointment_id = ?";
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
            throw new DatabaseException("Failed to find appointment by id", e);
        }
    }

    @Override
    public List<Appointment> findByDate(LocalDate date) {
        String sql = BASE_SELECT + " WHERE a.appointment_date = ? ORDER BY a.appointment_time";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find appointments by date", e);
        }
    }

    @Override
    public List<Appointment> findByPatientId(Long patientId) {
        String sql = BASE_SELECT + " WHERE a.patient_id = ? ORDER BY a.appointment_date DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find appointments by patient", e);
        }
    }

    @Override
    public List<Appointment> findByDentistId(Long dentistId) {
        String sql = BASE_SELECT + " WHERE a.dentist_id = ? ORDER BY a.appointment_date DESC, a.appointment_time";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find appointments by dentist", e);
        }
    }

    @Override
    public List<Appointment> findByStatus(String status) {
        String sql = BASE_SELECT + " WHERE a.status = ? ORDER BY a.appointment_date, a.appointment_time";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find appointments by status", e);
        }
    }

    @Override
    public String findLatestAppointmentNumber() {
        String sql = """
                SELECT appointment_number FROM appointments
                WHERE appointment_number LIKE ?
                ORDER BY appointment_id DESC LIMIT 1
                """;
        String pattern = "APT-" + LocalDate.now().getYear() + "-%";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("appointment_number");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find latest appointment number", e);
        }
    }

    @Override
    public Long insert(Appointment appointment) {
        String sql = """
                INSERT INTO appointments (appointment_number, patient_id, dentist_id, appointment_date,
                    appointment_time, reason, status, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindAppointment(ps, appointment);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new DatabaseException("Failed to retrieve generated appointment id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert appointment", e);
        }
    }

    @Override
    public void update(Appointment appointment) {
        String sql = """
                UPDATE appointments SET patient_id = ?, dentist_id = ?, appointment_date = ?,
                    appointment_time = ?, reason = ?, status = ?, notes = ?
                WHERE appointment_id = ?
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, appointment.getPatientId());
            ps.setLong(2, appointment.getDentistId());
            ps.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            ps.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            ps.setString(5, appointment.getReason());
            ps.setString(6, appointment.getStatus());
            ps.setString(7, appointment.getNotes());
            ps.setLong(8, appointment.getAppointmentId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update appointment", e);
        }
    }

    @Override
    public void updateStatus(Long appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, appointmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update appointment status", e);
        }
    }

    @Override
    public boolean existsForDentistAtTime(Long dentistId, LocalDate date, LocalTime time, Long excludeId) {
        String sql = """
                SELECT COUNT(*) FROM appointments
                WHERE dentist_id = ? AND appointment_date = ? AND appointment_time = ?
                AND status <> 'CANCELLED'
                """;
        if (excludeId != null) {
            sql += " AND appointment_id <> ?";
        }
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, dentistId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(time));
            if (excludeId != null) {
                ps.setLong(4, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to check dentist schedule conflict", e);
        }
    }

    @Override
    public int countByDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ?";
        return countQuery(sql, Date.valueOf(date));
    }

    @Override
    public int countByDateAndStatus(LocalDate date, String status) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ? AND status = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count appointments by date and status", e);
        }
    }

    private int countQuery(String sql, Date date) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count appointments", e);
        }
    }

    private List<Appointment> queryAppointments(String sql) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query appointments", e);
        }
    }

    private List<Appointment> mapList(ResultSet rs) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        while (rs.next()) {
            appointments.add(mapRow(rs));
        }
        return appointments;
    }

    private void bindAppointment(PreparedStatement ps, Appointment appointment) throws SQLException {
        ps.setString(1, appointment.getAppointmentNumber());
        ps.setLong(2, appointment.getPatientId());
        ps.setLong(3, appointment.getDentistId());
        ps.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
        ps.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
        ps.setString(6, appointment.getReason());
        ps.setString(7, appointment.getStatus());
        ps.setString(8, appointment.getNotes());
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getLong("appointment_id"));
        appointment.setAppointmentNumber(rs.getString("appointment_number"));
        appointment.setPatientId(rs.getLong("patient_id"));
        appointment.setDentistId(rs.getLong("dentist_id"));
        appointment.setAppointmentDate(JdbcMapper.toLocalDate(rs.getDate("appointment_date")));
        appointment.setAppointmentTime(JdbcMapper.toLocalTime(rs.getTime("appointment_time")));
        appointment.setReason(rs.getString("reason"));
        appointment.setStatus(rs.getString("status"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setCreatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("created_at")));
        appointment.setUpdatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("updated_at")));
        appointment.setPatientName(rs.getString("patient_name"));
        appointment.setDentistName(rs.getString("dentist_name"));
        return appointment;
    }
}
