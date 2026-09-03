package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.ReportSummary;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.service.ReportService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@WebServlet(name = "DashboardServlet")
public class DashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);

        try {
            LocalDate today = LocalDate.now();
            AppointmentService appointmentService = getService(ServiceFactory.APPOINTMENT_SERVICE, AppointmentService.class);
            ReportService reportService = getService(ServiceFactory.REPORT_SERVICE, ReportService.class);
            PatientService patientService = getService(ServiceFactory.PATIENT_SERVICE, PatientService.class);

            ReportSummary summary = reportService.getDailySummary(today);
            List<Appointment> todayAppointments = AppConstants.ROLE_DENTIST.equals(user.getRole())
                    ? appointmentService.getAppointmentsForDentist(user.getUserId()).stream()
                        .filter(a -> today.equals(a.getAppointmentDate()))
                        .toList()
                    : appointmentService.getAppointmentsByDate(today);

            List<Appointment> upcomingAppointments = appointmentService.getAllAppointments().stream()
                    .filter(a -> !a.getAppointmentDate().isBefore(today))
                    .filter(a -> AppConstants.STATUS_SCHEDULED.equals(a.getStatus()))
                    .filter(a -> !AppConstants.ROLE_DENTIST.equals(user.getRole())
                            || user.getUserId().equals(a.getDentistId()))
                    .sorted(Comparator.comparing(Appointment::getAppointmentDate)
                            .thenComparing(Appointment::getAppointmentTime))
                    .limit(5)
                    .toList();

            request.setAttribute("totalPatientCount", patientService.getActivePatients().size());
            request.setAttribute("todayAppointmentCount", todayAppointments.size());
            request.setAttribute("completedTodayCount", summary.getCompletedAppointments());
            request.setAttribute("todayRevenue", summary.getTotalCollected());
            request.setAttribute("upcomingAppointments", upcomingAppointments);
            request.setAttribute("dailySummary", summary);

            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
        } catch (DatabaseException ex) {
            request.setAttribute("errorMessage",
                    "Database error: start MySQL, verify db.properties, and run database/sunrise_dental_clinic.sql");
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }
}