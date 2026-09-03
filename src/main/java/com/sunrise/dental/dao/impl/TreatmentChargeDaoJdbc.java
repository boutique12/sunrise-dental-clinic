package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.TreatmentChargeDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.TreatmentCharge;
import com.sunrise.dental.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreatmentChargeDaoJdbc implements TreatmentChargeDao {

    private final DatabaseConnection databaseConnection;

    public TreatmentChargeDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<TreatmentCharge> findAllActive() {
        String sql = "SELECT * FROM treatment_charges WHERE active = TRUE ORDER BY treatment_name";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<TreatmentCharge> charges = new ArrayList<>();
            while (rs.next()) {
                charges.add(mapRow(rs));
            }
            return charges;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment charges", e);
        }
    }

    @Override
    public Optional<TreatmentCharge> findById(Long chargeId) {
        String sql = "SELECT * FROM treatment_charges WHERE charge_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, chargeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment charge by id", e);
        }
    }

    @Override
    public Optional<TreatmentCharge> findByCode(String treatmentCode) {
        String sql = "SELECT * FROM treatment_charges WHERE treatment_code = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, treatmentCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment charge by code", e);
        }
    }

    private TreatmentCharge mapRow(ResultSet rs) throws SQLException {
        TreatmentCharge charge = new TreatmentCharge();
        charge.setChargeId(rs.getLong("charge_id"));
        charge.setTreatmentCode(rs.getString("treatment_code"));
        charge.setTreatmentName(rs.getString("treatment_name"));
        charge.setDescription(rs.getString("description"));
        charge.setStandardCharge(rs.getBigDecimal("standard_charge"));
        charge.setActive(rs.getBoolean("active"));
        charge.setCreatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("created_at")));
        return charge;
    }
}
