package com.sunrise.dental.dao;

import com.sunrise.dental.model.ReportSummary;

import java.time.LocalDate;

public interface ReportDao {

    ReportSummary generateDailySummary(LocalDate date);
}
