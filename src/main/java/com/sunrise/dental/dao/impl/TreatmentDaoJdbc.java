package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.TreatmentDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.util.DatabaseConnection;

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

public class TreatmentDaoJdbc implements TreatmentDao {

    private static final String BASE_SELECT = """
            SELECT t.*,
                   CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
                   u.full_name AS dentist_name,
                   a.appointment_number
            FROM treatments t
            JOIN appointments a ON t.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN users u ON t.dentist_id = u.user_id
            """;

    private final DatabaseConnection databaseConnection;

    public TreatmentDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Treatment> findAll() {
        return queryTreatments(BASE_SELECT + " ORDER BY t.treatment_date DESC, t.treatment_id DESC");
    }

    @Override
    public Optional<Treatment> findById(Long treatmentId) {
        String sql = BASE_SELECT + " WHERE t.treatment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, treatmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment by id", e);
        }
    }

    @Override
    public Optional<Treatment> findByAppointmentId(Long appointmentId) {
        String sql = BASE_SELECT + " WHERE t.appointment_id = ?";
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
            throw new DatabaseException("Failed to find treatment by appointment", e);
        }
    }

    @Override
    public List<Treatment> findByDentistId(Long dentistId) {
        String sql = BASE_SELECT + " WHERE t.dentist_id = ? ORDER BY t.treatment_date DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, dentistId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapList(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatments by dentist", e);
        }
    }

    @Override
    public Long insert(Treatment treatment) {
        String sql = """
                INSERT INTO treatments (appointment_id, dentist_id, treatment_date, diagnosis,
                    treatment_notes, prescription)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, treatment.getAppointmentId());
            ps.setLong(2, treatment.getDentistId());
            ps.setDate(3, Date.valueOf(treatment.getTreatmentDate()));
            ps.setString(4, treatment.getDiagnosis());
            ps.setString(5, treatment.getTreatmentNotes());
            ps.setString(6, treatment.getPrescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new DatabaseException("Failed to retrieve generated treatment id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert treatment", e);
        }
    }

    @Override
    public void update(Treatment treatment) {
        String sql = """
                UPDATE treatments SET appointment_id = ?, dentist_id = ?, treatment_date = ?,
                    diagnosis = ?, treatment_notes = ?, prescription = ?
                WHERE treatment_id = ?
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, treatment.getAppointmentId());
            ps.setLong(2, treatment.getDentistId());
            ps.setDate(3, Date.valueOf(treatment.getTreatmentDate()));
            ps.setString(4, treatment.getDiagnosis());
            ps.setString(5, treatment.getTreatmentNotes());
            ps.setString(6, treatment.getPrescription());
            ps.setLong(7, treatment.getTreatmentId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update treatment", e);
        }
    }

    @Override
    public void delete(Long treatmentId) {
        String sql = "DELETE FROM treatments WHERE treatment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, treatmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete treatment", e);
        }
    }

    @Override
    public int countByDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM treatments WHERE treatment_date = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count treatments by date", e);
        }
    }

    private List<Treatment> queryTreatments(String sql) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return mapList(rs);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query treatments", e);
        }
    }

    private List<Treatment> mapList(ResultSet rs) throws SQLException {
        List<Treatment> treatments = new ArrayList<>();
        while (rs.next()) {
            treatments.add(mapRow(rs));
        }
        return treatments;
    }

    private Treatment mapRow(ResultSet rs) throws SQLException {
        Treatment treatment = new Treatment();
        treatment.setTreatmentId(rs.getLong("treatment_id"));
        treatment.setAppointmentId(rs.getLong("appointment_id"));
        treatment.setDentistId(rs.getLong("dentist_id"));
        treatment.setTreatmentDate(JdbcMapper.toLocalDate(rs.getDate("treatment_date")));
        treatment.setDiagnosis(rs.getString("diagnosis"));
        treatment.setTreatmentNotes(rs.getString("treatment_notes"));
        treatment.setPrescription(rs.getString("prescription"));
        treatment.setCreatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("created_at")));
        treatment.setUpdatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("updated_at")));
        treatment.setPatientName(rs.getString("patient_name"));
        treatment.setDentistName(rs.getString("dentist_name"));
        treatment.setAppointmentNumber(rs.getString("appointment_number"));
        return treatment;
    }
}
