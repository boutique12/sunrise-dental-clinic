package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.UserDao;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoJdbc implements UserDao {

    private final DatabaseConnection databaseConnection;

    public UserDaoJdbc(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT user_id, username, password_hash, full_name, role, email, phone, active,
                       created_at, updated_at
                FROM users
                WHERE username = ?
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by username", e);
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        String sql = """
                SELECT user_id, username, password_hash, full_name, role, email, phone, active,
                       created_at, updated_at
                FROM users
                WHERE user_id = ?
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by id", e);
        }
    }

    @Override
    public List<User> findByRole(String role) {
        String sql = """
                SELECT user_id, username, password_hash, full_name, role, email, phone, active,
                       created_at, updated_at
                FROM users
                WHERE role = ? AND active = TRUE
                ORDER BY full_name
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find users by role", e);
        }
    }

    @Override
    public List<User> findAllActive() {
        String sql = """
                SELECT user_id, username, password_hash, full_name, role, email, phone, active,
                       created_at, updated_at
                FROM users
                WHERE active = TRUE
                ORDER BY full_name
                """;
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find active users", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("active"));
        user.setCreatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("created_at")));
        user.setUpdatedAt(JdbcMapper.toLocalDateTime(rs.getTimestamp("updated_at")));
        return user;
    }
}
