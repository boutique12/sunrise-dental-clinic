package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet(name = "PatientServlet")
public class PatientServlet extends BaseServlet {

    private PatientService patientService() {
        return getService(ServiceFactory.PATIENT_SERVICE, PatientService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        ServletUtil.applyFlashMessage(request);
        ServletUtil.applyError(request);

        String action = ServletUtil.pathAction(request);
        switch (action) {
            case "list" -> listPatients(request, response);
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
                case "create" -> createPatient(request, response);
                case "update" -> updatePatient(request, response);
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (ValidationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ServletUtil.applyError(request);
            request.setAttribute("patient", bindPatient(request));
            request.getRequestDispatcher("/WEB-INF/views/patients/form.jsp").forward(request, response);
        }
    }

    private void listPatients(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        if (search != null && !search.isBlank()) {
            request.setAttribute("patients", patientService().searchPatients(search));
        } else {
            request.setAttribute("patients", patientService().getActivePatients());
        }
        request.getRequestDispatcher("/WEB-INF/views/patients/list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("patient", null);
        request.getRequestDispatcher("/WEB-INF/views/patients/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long patientId = parseLong(request.getParameter("id"));
        Optional<Patient> patient = patientService().getPatientById(patientId);
        if (patient.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Patient not found");
            return;
        }
        request.setAttribute("patient", patient.get());
        request.getRequestDispatcher("/WEB-INF/views/patients/form.jsp").forward(request, response);
    }

    private void createPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Patient patient = bindPatient(request);
        patientService().createPatient(patient);
        response.sendRedirect(request.getContextPath() + "/patients?message=Patient+created+successfully");
    }

    private void updatePatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Patient patient = bindPatient(request);
        patient.setPatientId(parseLong(request.getParameter("patientId")));
        patientService().updatePatient(patient);
        response.sendRedirect(request.getContextPath() + "/patients?message=Patient+updated+successfully");
    }

    private Patient bindPatient(HttpServletRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.getParameter("firstName"));
        patient.setLastName(request.getParameter("lastName"));
        String dob = request.getParameter("dateOfBirth");
        if (dob != null && !dob.isBlank()) {
            patient.setDateOfBirth(LocalDate.parse(dob));
        }
        patient.setGender(request.getParameter("gender"));
        patient.setNicNumber(request.getParameter("nicNumber"));
        patient.setPhone(request.getParameter("phone"));
        patient.setEmail(request.getParameter("email"));
        patient.setAddress(request.getParameter("address"));
        patient.setMedicalNotes(request.getParameter("medicalNotes"));
        patient.setActive(!"false".equalsIgnoreCase(request.getParameter("active")));
        return patient;
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Invalid id parameter");
        }
        return Long.parseLong(value);
    }
}
