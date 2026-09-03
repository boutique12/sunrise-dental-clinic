package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.model.TreatmentCharge;
import com.sunrise.dental.model.TreatmentDetail;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.TreatmentChargeService;
import com.sunrise.dental.service.TreatmentService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "TreatmentServlet")
public class TreatmentServlet extends BaseServlet {

    private TreatmentService treatmentService() {
        return getService(ServiceFactory.TREATMENT_SERVICE, TreatmentService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        ServletUtil.applyFlashMessage(request);
        ServletUtil.applyError(request);

        String action = ServletUtil.pathAction(request);
        switch (action) {
            case "list" -> listTreatments(request, response);
            case "new" -> showNewForm(request, response);
            case "edit", "view" -> showTreatmentForm(request, response, "view".equals(action));
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
                case "create" -> createTreatment(request, response);
                case "update" -> updateTreatment(request, response);
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (ValidationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ServletUtil.applyError(request);
            preserveTreatmentForm(request, action);
            populateFormData(request);
            request.getRequestDispatcher("/WEB-INF/views/treatments/form.jsp").forward(request, response);
        }
    }

    private void listTreatments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);
        List<Treatment> treatments;
        if (AppConstants.ROLE_DENTIST.equals(user.getRole())) {
            treatments = treatmentService().getTreatmentsForDentist(user.getUserId());
        } else {
            treatments = treatmentService().getAllTreatments();
        }

        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        if (fromDate != null && !fromDate.isBlank()) {
            LocalDate from = LocalDate.parse(fromDate);
            treatments = treatments.stream()
                    .filter(t -> t.getTreatmentDate() != null && !t.getTreatmentDate().isBefore(from))
                    .collect(Collectors.toList());
        }
        if (toDate != null && !toDate.isBlank()) {
            LocalDate to = LocalDate.parse(toDate);
            treatments = treatments.stream()
                    .filter(t -> t.getTreatmentDate() != null && !t.getTreatmentDate().isAfter(to))
                    .collect(Collectors.toList());
        }

        request.setAttribute("treatments", treatments);
        request.getRequestDispatcher("/WEB-INF/views/treatments/list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("defaultTreatmentDate", LocalDate.now());
        request.removeAttribute("treatment");

        String appointmentId = request.getParameter("appointmentId");
        if (appointmentId != null && !appointmentId.isBlank()) {
            try {
                request.setAttribute("selectedAppointmentId", Long.parseLong(appointmentId.trim()));
            } catch (NumberFormatException ex) {
                request.setAttribute("errorMessage", "Invalid appointment link — choose an appointment from the list");
                ServletUtil.applyError(request);
            }
        }

        populateFormData(request);
        request.getRequestDispatcher("/WEB-INF/views/treatments/form.jsp").forward(request, response);
    }

    private void showTreatmentForm(HttpServletRequest request, HttpServletResponse response, boolean readOnly)
            throws ServletException, IOException {
        Long id = parseLong(request.getParameter("id"), "treatment");
        Optional<Treatment> treatment = treatmentService().getTreatmentById(id);
        if (treatment.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Treatment not found");
            return;
        }
        request.setAttribute("treatment", treatment.get());
        request.setAttribute("readOnly", readOnly);
        populateFormData(request);
        request.getRequestDispatcher("/WEB-INF/views/treatments/form.jsp").forward(request, response);
    }

    private void populateFormData(HttpServletRequest request) {
        AppointmentService appointmentService = getService(ServiceFactory.APPOINTMENT_SERVICE, AppointmentService.class);
        TreatmentChargeService chargeService = getService(ServiceFactory.TREATMENT_CHARGE_SERVICE, TreatmentChargeService.class);
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);
        request.setAttribute("appointments", appointmentService.getAllAppointments().stream()
                .filter(a -> AppConstants.STATUS_SCHEDULED.equals(a.getStatus()))
                .filter(a -> user == null || AppConstants.ROLE_RECEPTIONIST.equals(user.getRole())
                        || user.getUserId().equals(a.getDentistId()))
                .collect(Collectors.toList()));
        request.setAttribute("charges", chargeService.getActiveCharges());
    }

    private void preserveTreatmentForm(HttpServletRequest request, String action) {
        if ("list".equals(action)) {
            action = "create";
        }
        if (!"update".equals(action)) {
            return;
        }

        Treatment treatment = new Treatment();
        String treatmentId = request.getParameter("treatmentId");
        if (treatmentId != null && !treatmentId.isBlank()) {
            try {
                treatment.setTreatmentId(Long.parseLong(treatmentId.trim()));
            } catch (NumberFormatException ignored) {
                return;
            }
        } else {
            return;
        }

        String appointmentId = request.getParameter("appointmentId");
        if (appointmentId != null && !appointmentId.isBlank()) {
            try {
                treatment.setAppointmentId(Long.parseLong(appointmentId.trim()));
            } catch (NumberFormatException ignored) {
            }
        }

        String treatmentDate = request.getParameter("treatmentDate");
        if (treatmentDate != null && !treatmentDate.isBlank()) {
            try {
                treatment.setTreatmentDate(LocalDate.parse(treatmentDate.trim()));
            } catch (Exception ignored) {
            }
        }

        treatment.setDiagnosis(request.getParameter("diagnosis"));
        treatment.setTreatmentNotes(request.getParameter("treatmentNotes"));
        treatment.setPrescription(request.getParameter("prescription"));
        request.setAttribute("treatment", treatment);
    }

    private void createTreatment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Treatment treatment = bindTreatment(request);
        treatmentService().createTreatment(treatment);
        response.sendRedirect(request.getContextPath() + "/treatments?message=Treatment+recorded");
    }

    private void updateTreatment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Treatment treatment = bindTreatment(request, true);
        treatmentService().updateTreatment(treatment);
        response.sendRedirect(request.getContextPath() + "/treatments?message=Treatment+updated");
    }

    private Treatment bindTreatment(HttpServletRequest request) {
        return bindTreatment(request, false);
    }

    private Treatment bindTreatment(HttpServletRequest request, boolean includeTreatmentId) {
        Treatment treatment = new Treatment();
        if (includeTreatmentId) {
            treatment.setTreatmentId(parseLong(request.getParameter("treatmentId"), "treatment"));
        }
        treatment.setAppointmentId(parseLong(request.getParameter("appointmentId"), "appointment"));
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);
        treatment.setDentistId(user.getUserId());
        treatment.setTreatmentDate(parseDate(request.getParameter("treatmentDate")));
        treatment.setDiagnosis(request.getParameter("diagnosis"));
        treatment.setTreatmentNotes(request.getParameter("treatmentNotes"));
        treatment.setPrescription(request.getParameter("prescription"));
        treatment.setDetails(bindDetails(request));
        return treatment;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Treatment date is required");
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            throw new ValidationException("Invalid treatment date");
        }
    }

    private List<TreatmentDetail> bindDetails(HttpServletRequest request) {
        String[] chargeIds = request.getParameterValues("chargeIds");
        if (chargeIds == null || chargeIds.length == 0) {
            chargeIds = request.getParameterValues("chargeId");
        }

        TreatmentChargeService chargeService = getService(ServiceFactory.TREATMENT_CHARGE_SERVICE, TreatmentChargeService.class);
        Map<Long, TreatmentCharge> chargeMap = chargeService.getActiveCharges().stream()
                .collect(Collectors.toMap(TreatmentCharge::getChargeId, c -> c));

        List<TreatmentDetail> details = new ArrayList<>();
        if (chargeIds == null) {
            return details;
        }

        for (String chargeIdValue : chargeIds) {
            if (chargeIdValue == null || chargeIdValue.isBlank()) {
                continue;
            }
            Long chargeId = Long.parseLong(chargeIdValue);
            TreatmentCharge charge = chargeMap.get(chargeId);
            if (charge == null) {
                continue;
            }
            String quantityValue = request.getParameter("quantity_" + chargeId);
            int quantity = 1;
            if (quantityValue != null && !quantityValue.isBlank()) {
                quantity = Integer.parseInt(quantityValue);
            }
            TreatmentDetail detail = new TreatmentDetail();
            detail.setChargeId(chargeId);
            detail.setQuantity(quantity);
            detail.setUnitPrice(charge.getStandardCharge());
            details.add(detail);
        }
        return details;
    }

    private Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Please select a " + fieldName + " from the list");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid " + fieldName + " value");
        }
    }
}