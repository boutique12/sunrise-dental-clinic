package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.TreatmentDetailDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.TreatmentDetail;
import com.sunrise.dental.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDetailDaoJdbc implements TreatmentDetailDao {

    private final DatabaseConnection databaseConnection;

    public TreatmentDetailDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public List<TreatmentDetail> findByTreatmentId(Long treatmentId) {
        String sql = """
                SELECT td.*, tc.treatment_name, tc.treatment_code
                FROM treatment_details td
                JOIN treatment_charges tc ON td.charge_id = tc.charge_id
                WHERE td.treatment_id = ?
                ORDER BY td.treatment_detail_id
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, treatmentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<TreatmentDetail> details = new ArrayList<>();
                while (rs.next()) {
                    details.add(mapRow(rs));
                }
                return details;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment details", e);
        }
    }

    @Override
    public void insert(TreatmentDetail detail) {
        String sql = """
                INSERT INTO treatment_details (treatment_id, charge_id, quantity, unit_price, notes)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, detail.getTreatmentId());
            ps.setLong(2, detail.getChargeId());
            ps.setInt(3, detail.getQuantity());
            ps.setBigDecimal(4, detail.getUnitPrice());
            ps.setString(5, detail.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert treatment detail", e);
        }
    }

    @Override
    public void deleteByTreatmentId(Long treatmentId) {
        String sql = "DELETE FROM treatment_details WHERE treatment_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, treatmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete treatment details", e);
        }
    }

    private TreatmentDetail mapRow(ResultSet rs) throws SQLException {
        TreatmentDetail detail = new TreatmentDetail();
        detail.setTreatmentDetailId(rs.getLong("treatment_detail_id"));
        detail.setTreatmentId(rs.getLong("treatment_id"));
        detail.setChargeId(rs.getLong("charge_id"));
        detail.setQuantity(rs.getInt("quantity"));
        detail.setUnitPrice(rs.getBigDecimal("unit_price"));
        detail.setNotes(rs.getString("notes"));
        detail.setTreatmentName(rs.getString("treatment_name"));
        detail.setTreatmentCode(rs.getString("treatment_code"));
        return detail;
    }
}
