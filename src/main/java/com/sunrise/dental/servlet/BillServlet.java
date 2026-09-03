package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.BillService;
import com.sunrise.dental.service.TreatmentService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "BillServlet")
public class BillServlet extends BaseServlet {

    private BillService billService() {
        return getService(ServiceFactory.BILL_SERVICE, BillService.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletUtil.setNoCacheHeaders(response);
        ServletUtil.applyFlashMessage(request);
        ServletUtil.applyError(request);

        String action = ServletUtil.pathAction(request);
        switch (action) {
            case "list" -> listBills(request, response);
            case "new" -> showNewForm(request, response);
            case "edit" -> showEditForm(request, response);
            case "print" -> showPrintView(request, response);
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
                case "create" -> createBill(request, response);
                case "update" -> updateBill(request, response);
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (ValidationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ServletUtil.applyError(request);
            showNewForm(request, response);
        }
    }

    private void listBills(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String paymentStatus = request.getParameter("paymentStatus");
        List<Bill> bills = billService().getAllBills();
        if (paymentStatus != null && !paymentStatus.isBlank()) {
            bills = bills.stream()
                    .filter(b -> paymentStatus.equalsIgnoreCase(b.getPaymentStatus()))
                    .collect(Collectors.toList());
        }
        request.setAttribute("bills", bills);
        request.getRequestDispatcher("/WEB-INF/views/bills/list.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("bill", null);
        request.setAttribute("appointments", getBillableAppointments());
        request.getRequestDispatcher("/WEB-INF/views/bills/form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Bill bill = loadBill(request, response);
        if (bill == null) {
            return;
        }
        request.setAttribute("bill", bill);
        request.setAttribute("appointments", getCompletedAppointments());
        loadBillDetails(request, bill);
        request.getRequestDispatcher("/WEB-INF/views/bills/form.jsp").forward(request, response);
    }

    private void showPrintView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Bill bill = loadBill(request, response);
        if (bill == null) {
            return;
        }
        request.setAttribute("bill", bill);
        loadBillDetails(request, bill);
        request.getRequestDispatcher("/WEB-INF/views/bills/print.jsp").forward(request, response);
    }

    private Bill loadBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long billId = parseLong(request.getParameter("id"));
        Optional<Bill> bill = billService().getBillById(billId);
        if (bill.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Bill not found");
            return null;
        }
        return bill.get();
    }

    private void loadBillDetails(HttpServletRequest request, Bill bill) {
        TreatmentService treatmentService = getService(ServiceFactory.TREATMENT_SERVICE, TreatmentService.class);
        Optional<Treatment> treatment = treatmentService.getAllTreatments().stream()
                .filter(t -> bill.getAppointmentId().equals(t.getAppointmentId()))
                .findFirst();
        treatment.ifPresent(t -> request.setAttribute("billDetails", t.getDetails()));
    }

    private List<com.sunrise.dental.model.Appointment> getBillableAppointments() {
        return getCompletedAppointments().stream()
                .filter(a -> billService().getBillByAppointmentId(a.getAppointmentId()).isEmpty())
                .collect(Collectors.toList());
    }

    private List<com.sunrise.dental.model.Appointment> getCompletedAppointments() {
        AppointmentService appointmentService = getService(ServiceFactory.APPOINTMENT_SERVICE, AppointmentService.class);
        return appointmentService.getAllAppointments().stream()
                .filter(a -> AppConstants.STATUS_COMPLETED.equals(a.getStatus()))
                .collect(Collectors.toList());
    }

    private void createBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long appointmentId = parseLong(request.getParameter("appointmentId"));
        String discountParam = request.getParameter("discount");
        BigDecimal discount = discountParam != null && !discountParam.isBlank()
                ? new BigDecimal(discountParam) : BigDecimal.ZERO;
        billService().generateBillForAppointment(appointmentId, discount);
        response.sendRedirect(request.getContextPath() + "/bills?message=Bill+generated");
    }

    private void updateBill(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Bill bill = new Bill();
        bill.setBillId(parseLong(request.getParameter("billId")));
        bill.setSubtotal(new BigDecimal(request.getParameter("subtotal")));
        bill.setDiscount(new BigDecimal(request.getParameter("discount")));
        bill.setPaymentStatus(request.getParameter("paymentStatus"));
        billService().updateBill(bill);
        response.sendRedirect(request.getContextPath() + "/bills?message=Bill+updated");
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Invalid id parameter");
        }
        return Long.parseLong(value);
    }
}
