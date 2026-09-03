package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ClinicScheduleUtil;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "AppointmentServlet")
public class AppointmentServlet extends BaseServlet {

    private AppointmentService appointmentService() {
        return getService(ServiceFactory.APPOINTMENT_SERVICE, AppointmentService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        ServletUtil.applyFlashMessage(request);
        ServletUtil.applyError(request);

        String action = ServletUtil.pathAction(request);
        switch (action) {
            case "list" -> listAppointments(request, response);
            case "new" -> showNewForm(request, response);
            case "edit" -> showEditForm(request, response);
            default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        String action = ServletUtil.pathAction(request);
        if ("list".equals(action)) {
            action = "create";
        }
        try {
            switch (action) {
                case "create" -> createAppointment(request, response);
                case "update" -> updateAppointment(request, response);
                case "complete" -> completeAppointment(request, response);
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (ValidationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ServletUtil.applyError(request);
            request.setAttribute("appointment", bindAppointment(request,
                    request.getParameter("appointmentId") != null && !request.getParameter("appointmentId").isBlank()));
            populateFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/appointments/form.jsp").forward(request, response);
        }
    }

    private void listAppointments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);
        String dateParam = request.getParameter("date");
        String statusParam = request.getParameter("status");
        String patientIdParam = request.getParameter("patientId");

        List<Appointment> appointments;
        if (dateParam != null && !dateParam.isBlank()) {
            appointments = appointmentService().getAppointmentsByDate(LocalDate.parse(dateParam));
        } else if (patientIdParam != null && !patientIdParam.isBlank()) {
            appointments = appointmentService().getAllAppointments().stream()
                    .filter(a -> a.getPatientId().equals(Long.parseLong(patientIdParam)))
                    .collect(Collectors.toList());
        } else if (AppConstants.ROLE_DENTIST.equals(user.getRole())) {
            appointments = appointmentService().getAppointmentsForDentist(user.getUserId());
        } else {
            appointments = appointmentService().getAllAppointments();
        }

        if (statusParam != null && !statusParam.isBlank()) {
            appointments = appointments.stream()
                    .filter(a -> statusParam.equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
        }

        request.setAttribute("appointments", appointments);
        request.getRequestDispatcher("/WEB-INF/views/appointments/list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("appointment", null);
        populateFormData(request);
        request.getRequestDispatcher("/WEB-INF/views/appointments/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = parseLong(request.getParameter("id"), "appointment");
        Optional<Appointment> appointment = appointmentService().getAppointmentById(id);
        if (appointment.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Appointment not found");
            return;
        }
        request.setAttribute("appointment", appointment.get());
        populateFormData(request);
        request.getRequestDispatcher("/WEB-INF/views/appointments/form.jsp").forward(request, response);
    }

    private void populateFormData(HttpServletRequest request) {
        PatientService patientService = getService(ServiceFactory.PATIENT_SERVICE, PatientService.class);
        request.setAttribute("patients", patientService.getActivePatients());
        request.setAttribute("dentists", appointmentService().getAvailableDentists());
        request.setAttribute("timeSlotOptions", ClinicScheduleUtil.getAppointmentSlots().stream()
                .map(slot -> Map.of(
                        "value", slot.toString(),
                        "label", ClinicScheduleUtil.formatDisplayTime(slot)))
                .collect(Collectors.toList()));
    }

    private void completeAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"), "appointment");
        appointmentService().completeAppointment(id);
        response.sendRedirect(request.getContextPath() + "/appointments?message=Appointment+marked+completed");
    }

    private void createAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Appointment appointment = bindAppointment(request);
        appointmentService().createAppointment(appointment);
        response.sendRedirect(request.getContextPath() + "/appointments?message=Appointment+created");
    }

    private void updateAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Appointment appointment = bindAppointment(request, true);
        appointmentService().updateAppointment(appointment);
        response.sendRedirect(request.getContextPath() + "/appointments?message=Appointment+updated");
    }

    private Appointment bindAppointment(HttpServletRequest request) {
        return bindAppointment(request, false);
    }

    private Appointment bindAppointment(HttpServletRequest request, boolean includeAppointmentId) {
        Appointment appointment = new Appointment();
        if (includeAppointmentId) {
            appointment.setAppointmentId(parseLong(request.getParameter("appointmentId"), "appointment"));
        }
        appointment.setPatientId(parseLong(request.getParameter("patientId"), "patient"));
        appointment.setDentistId(parseLong(request.getParameter("dentistId"), "dentist"));
        appointment.setAppointmentDate(parseDate(request.getParameter("appointmentDate")));
        appointment.setAppointmentTime(parseTime(request.getParameter("appointmentTime")));
        appointment.setReason(request.getParameter("reason"));
        appointment.setStatus(request.getParameter("status"));
        appointment.setNotes(request.getParameter("notes"));
        return appointment;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Appointment date is required");
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            throw new ValidationException("Invalid appointment date");
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Appointment time is required");
        }
        try {
            LocalTime time = LocalTime.parse(value);
            ClinicScheduleUtil.validateAppointmentTime(time);
            return time;
        } catch (ValidationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ValidationException("Invalid appointment time");
        }
    }

    private Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Missing or invalid " + fieldName + " — open the appointment from the list and try again");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid " + fieldName + " value");
        }
    }
}