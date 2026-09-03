package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import com.sunrise.dental.exception.DatabaseException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Payment;
import com.sunrise.dental.model.User;
import com.sunrise.dental.service.BillService;
import com.sunrise.dental.service.PaymentService;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "PaymentServlet")
public class PaymentServlet extends BaseServlet {

    private PaymentService paymentService() {
        return getService(ServiceFactory.PAYMENT_SERVICE, PaymentService.class);
    }

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
        try {
            switch (action) {
                case "list" -> listPayments(request, response);
                case "new" -> showNewForm(request, response);
                case "view" -> showPaymentView(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ServletUtil.applyError(request);
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
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
            if ("create".equals(action)) {
                Payment payment = bindPayment(request);
                paymentService().recordPayment(payment);
                response.sendRedirect(request.getContextPath() + "/payments?message=Payment+recorded");
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
            }
        } catch (ValidationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ServletUtil.applyError(request);
            populatePaymentForm(request);
            request.getRequestDispatcher("/WEB-INF/views/payments/form.jsp").forward(request, response);
        } catch (DatabaseException ex) {
            request.setAttribute("errorMessage", "Could not save payment. Please verify the bill and try again.");
            ServletUtil.applyError(request);
            populatePaymentForm(request);
            request.getRequestDispatcher("/WEB-INF/views/payments/form.jsp").forward(request, response);
        }
    }

    private void listPayments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Payment> payments = paymentService().getAllPayments();
            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");
            if (fromDate != null && !fromDate.isBlank()) {
                LocalDate from = LocalDate.parse(fromDate);
                payments = payments.stream()
                        .filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().toLocalDate().isBefore(from))
                        .collect(Collectors.toList());
            }
            if (toDate != null && !toDate.isBlank()) {
                LocalDate to = LocalDate.parse(toDate);
                payments = payments.stream()
                        .filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().toLocalDate().isAfter(to))
                        .collect(Collectors.toList());
            }
            request.setAttribute("payments", payments);
            request.getRequestDispatcher("/WEB-INF/views/payments/list.jsp").forward(request, response);
        } catch (DatabaseException ex) {
            request.setAttribute("errorMessage", "Could not load payments. Please check MySQL is running.");
            request.getRequestDispatcher("/WEB-INF/views/error/500.jsp").forward(request, response);
        }
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        populatePaymentForm(request);
        request.getRequestDispatcher("/WEB-INF/views/payments/form.jsp").forward(request, response);
    }

    private void populatePaymentForm(HttpServletRequest request) {
        List<Bill> unpaidBills = billService().getAllBills().stream()
                .filter(b -> !AppConstants.PAYMENT_PAID.equals(b.getPaymentStatus()))
                .collect(Collectors.toList());
        request.setAttribute("unpaidBills", unpaidBills);

        String billIdParam = request.getParameter("billId");
        if (billIdParam != null && !billIdParam.isBlank()) {
            try {
                Long billId = Long.parseLong(billIdParam.trim());
                billService().getBillById(billId).ifPresent(bill -> request.setAttribute("selectedBill", bill));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void showPaymentView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long paymentId = parseLong(request.getParameter("id"), "payment");
        Optional<Payment> payment = paymentService().getPaymentById(paymentId);
        if (payment.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Payment not found");
            return;
        }
        Payment paymentRecord = payment.get();
        request.setAttribute("payment", paymentRecord);
        billService().getBillById(paymentRecord.getBillId())
                .ifPresent(bill -> request.setAttribute("billId", bill.getBillId()));
        request.getRequestDispatcher("/WEB-INF/views/payments/view.jsp").forward(request, response);
    }

    private Payment bindPayment(HttpServletRequest request) {
        Payment payment = new Payment();
        payment.setBillId(parseLong(request.getParameter("billId"), "bill"));
        payment.setAmount(parseAmount(request.getParameter("amount")));
        payment.setPaymentMethod(parseRequiredText(request.getParameter("paymentMethod"), "payment method"));
        payment.setReferenceNumber(request.getParameter("referenceNumber"));
        payment.setNotes(request.getParameter("notes"));
        User user = (User) request.getSession().getAttribute(AppConstants.SESSION_USER);
        if (user == null) {
            throw new ValidationException("You must be logged in to record a payment");
        }
        payment.setReceivedBy(user.getUserId());
        return payment;
    }

    private BigDecimal parseAmount(String value) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Payment amount is required");
        }
        try {
            BigDecimal amount = new BigDecimal(value.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException("Payment amount must be greater than zero");
            }
            return amount;
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid payment amount");
        }
    }

    private String parseRequiredText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Please select a " + fieldName);
        }
        return value.trim();
    }

    private Long parseLong(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Please select a " + fieldName + " from the list");
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("Invalid " + fieldName + " value");
        }
    }
}