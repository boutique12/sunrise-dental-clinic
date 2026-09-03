package com.sunrise.dental.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

final class JdbcMapper {

    private JdbcMapper() {
    }

    static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    static LocalDate toLocalDate(java.sql.Date date) {
        return date != null ? date.toLocalDate() : null;
    }

    static LocalTime toLocalTime(java.sql.Time time) {
        return time != null ? time.toLocalTime() : null;
    }

    static String getString(ResultSet rs, String column) throws SQLException {
        return rs.getString(column);
    }
}
