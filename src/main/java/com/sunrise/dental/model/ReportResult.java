package com.sunrise.dental.model;

import java.util.ArrayList;
import java.util.List;

public class ReportResult {

    private String reportTitle;
    private List<String> reportHeaders = new ArrayList<>();
    private List<List<String>> reportData = new ArrayList<>();

    public ReportResult() {
    }

    public ReportResult(String reportTitle, List<String> reportHeaders, List<List<String>> reportData) {
        this.reportTitle = reportTitle;
        this.reportHeaders = reportHeaders;
        this.reportData = reportData;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public List<String> getReportHeaders() {
        return reportHeaders;
    }

    public void setReportHeaders(List<String> reportHeaders) {
        this.reportHeaders = reportHeaders;
    }

    public List<List<String>> getReportData() {
        return reportData;
    }

    public void setReportData(List<List<String>> reportData) {
        this.reportData = reportData;
    }
}
