package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.model.ReportResult;
import com.sunrise.dental.service.ReportService;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "ReportServlet")
public class ReportServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        ServletUtil.applyFlashMessage(request);

        ReportService reportService = getService(ServiceFactory.REPORT_SERVICE, ReportService.class);
        String action = ServletUtil.pathAction(request);

        if ("list".equals(action)) {
            request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp").forward(request, response);
            return;
        }

        ReportResult result = switch (action) {
            case "appointments" -> reportService.generateAppointmentsReport(
                    parseDate(request.getParameter("fromDate")),
                    parseDate(request.getParameter("toDate")));
            case "revenue" -> reportService.generateRevenueReport(
                    parseDate(request.getParameter("fromDate")),
                    parseDate(request.getParameter("toDate")));
            case "patients" -> reportService.generatePatientsReport(
                    parseOptionalDate(request.getParameter("fromDate")),
                    parseOptionalDate(request.getParameter("toDate")));
            case "treatments" -> reportService.generateTreatmentsReport(
                    parseDate(request.getParameter("fromDate")),
                    parseDate(request.getParameter("toDate")));
            case "payments" -> reportService.generatePaymentsReport(
                    parseDate(request.getParameter("fromDate")),
                    parseDate(request.getParameter("toDate")));
            case "outstanding-bills" -> reportService.generateOutstandingBillsReport();
            default -> null;
        };

        if (result == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("reportGenerated", Boolean.TRUE);
        request.setAttribute("reportType", action);
        request.setAttribute("reportTitle", result.getReportTitle());
        request.setAttribute("reportHeaders", result.getReportHeaders());
        request.setAttribute("reportData", result.getReportData());
        request.setAttribute("reportFromDate", request.getParameter("fromDate"));
        request.setAttribute("reportToDate", request.getParameter("toDate"));
        request.getRequestDispatcher("/WEB-INF/views/reports/index.jsp").forward(request, response);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }
        return LocalDate.parse(value);
    }

    private LocalDate parseOptionalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }
}