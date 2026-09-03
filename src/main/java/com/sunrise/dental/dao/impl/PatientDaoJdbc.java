package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.PatientDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.Patient;
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

public class PatientDaoJdbc implements PatientDao {

    private final DatabaseConnection databaseConnection;

    public PatientDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<Patient> findAll() {
        return queryPatients("SELECT * FROM patients ORDER BY last_name, first_name");
    }

    @Override
    public List<Patient> findAllActive() {
        return queryPatients("SELECT * FROM patients WHERE active = TRUE ORDER BY last_name, first_name");
    }

    @Override
    public Optional<Patient> findById(Long patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find patient by id", e);
        }
    }

    @Override
    public Optional<Patient> findByPatientNumber(String patientNumber) {
        String sql = "SELECT * FROM patients WHERE patient_number = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patientNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find patient by number", e);
        }
    }

    @Override
    public List<Patient> searchByNameOrPhone(String keyword) {
        String sql = """
                SELECT * FROM patients
                WHERE active = TRUE AND (
                    first_name LIKE ? OR last_name LIKE ? OR phone LIKE ?
                    OR patient_number LIKE ? OR nic_number LIKE ?
                )
                ORDER BY last_name, first_name
                """;
        String pattern = "%" + keyword + "%";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Patient> patients = new ArrayList<>();
                while (rs.next()) {
                    patients.add(mapRow(rs));
                }
                return patients;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search patients", e);
        }
    }

    @Override
    public String findLatestPatientNumber() {
        String sql = """
                SELECT patient_number FROM patients
                WHERE patient_number LIKE ?
                ORDER BY patient_id DESC LIMIT 1
                """;
        String pattern = "PAT-" + LocalDate.now().getYear() + "-%";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("patient_number");
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find latest patient number", e);
        }
    }

    @Override
    public Long insert(Patient patient) {
        String sql = """
                INSERT INTO patients (patient_number, first_name, last_name, date_of_birth, gender,
                    nic_number, phone, email, address, medical_notes, active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindPatient(ps, patient);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new DatabaseException("Failed to retrieve generated patient id");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert patient", e);
        }
    }

    @Override
    public void update(Patient patient) {
        String sql = """
                UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, gender = ?,
                    nic_number = ?, phone = ?, email = ?, address = ?, medical_notes = ?, active = ?
                WHERE patient_id = ?
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, patient.getFirstName());
            ps.setString(2, patient.getLastName());
            ps.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            ps.setString(4, patient.getGender());
            ps.setString(5, patient.getNicNumber());
            ps.setString(6, patient.getPhone());
            ps.setString(7, patient.getEmail());
            ps.setString(8, patient.getAddress());
            ps.setString(9, patient.getMedicalNotes());
            ps.setBoolean(10, patient.isActive());
            ps.setLong(11, patient.getPatientId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update patient", e);
        }
    }

    @Override
    public void deactivate(Long patientId) {
        String sql = "UPDATE patients SET active = FALSE WHERE patient_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to deactivate patient", e);
        }
    }

    @Override
    public int countCreatedOnDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM patients WHERE DATE(created_at) = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count patients created on date", e);
        }
    }

    private List<Patient> queryPatients(String sql) {
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Patient> patients = new ArrayList<>();
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
            return patients;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to query patients", e);
        }
    }

    private void bindPatient(PreparedStatement ps, Patient patient) throws SQLException {
        ps.setString(1, patient.getPatientNumber());
        ps.setString(2, patient.getFirstName());
        ps.setString(3, patient.getLastName());
        ps.setDate(4, Date.valueOf(patient.getDateOfBirth()));
        ps.setString(5, patient.getGender());
        ps.setString(6, patient.getNicNumber());
        ps.setString(7, patient.getPhone());
        ps.setString(8, patient.getEmail());
        ps.setString(9, patient.getAddress());
        ps.setString(10, patient.getMedicalNotes());
        ps.setBoolean(11, patient.isActive());
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setPatientId(rs.getLong("patient_id"));
        patient.setPatientNumber(rs.getString("patient_number"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(JdbcMapper.toLocalDate(rs.getDate("date_of_birth")));
        patient.setGender(rs.getString("gender"));
        patient.setNicNumber(rs.getString("nic_number"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setMedicalNotes(rs.getString("medical_notes"));
        patient.setActive(rs.getBoolean("active"));
        patient.setCreatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("created_at")));
        patient.setUpdatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("updated_at")));
        return patient;
    }
}
