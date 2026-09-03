package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "AppointmentApiServlet")
public class AppointmentApiServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        AppointmentService appointmentService = getService(ServiceFactory.APPOINTMENT_SERVICE, AppointmentService.class);
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);

        List<Appointment> appointments;
        String dateParam = request.getParameter("date");
        String dentistIdParam = request.getParameter("dentistId");

        if (dateParam != null && !dateParam.isBlank()) {
            appointments = appointmentService.getAppointmentsByDate(LocalDate.parse(dateParam));
        } else if (dentistIdParam != null && !dentistIdParam.isBlank()) {
            appointments = appointmentService.getAppointmentsForDentist(Long.parseLong(dentistIdParam));
        } else if (AppConstants.ROLE_DENTIST.equals(user.getRole())) {
            appointments = appointmentService.getAppointmentsForDentist(user.getUserId());
        } else {
            appointments = appointmentService.getAllAppointments();
        }

        try (PrintWriter writer = response.getWriter()) {
            writer.write(toJson(appointments));
        }
    }

    private String toJson(List<Appointment> appointments) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append('{')
                    .append("\"appointmentId\":").append(a.getAppointmentId()).append(',')
                    .append("\"appointmentNumber\":\"").append(ServletUtil.escapeJson(a.getAppointmentNumber())).append("\",")
                    .append("\"patientId\":").append(a.getPatientId()).append(',')
                    .append("\"patientName\":\"").append(ServletUtil.escapeJson(a.getPatientName())).append("\",")
                    .append("\"dentistId\":").append(a.getDentistId()).append(',')
                    .append("\"dentistName\":\"").append(ServletUtil.escapeJson(a.getDentistName())).append("\",")
                    .append("\"appointmentDate\":\"").append(a.getAppointmentDate()).append("\",")
                    .append("\"appointmentTime\":\"").append(a.getAppointmentTime()).append("\",")
                    .append("\"reason\":\"").append(ServletUtil.escapeJson(a.getReason())).append("\",")
                    .append("\"status\":\"").append(ServletUtil.escapeJson(a.getStatus())).append("\",")
                    .append("\"notes\":\"").append(ServletUtil.escapeJson(a.getNotes())).append('"')
                    .append('}');
        }
        json.append(']');
        return json.toString();
    }
}
