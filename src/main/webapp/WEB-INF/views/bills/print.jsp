<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Print Bill" scope="request" />
<c:set var="activeNav" value="bills" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="print-page">
    <div class="print-header">
        <h1>🦷 Sunrise Dental Clinic</h1>
        <p>123 Dental Avenue, Colombo &nbsp;|&nbsp; Tel: 011 234 5678 &nbsp;|&nbsp; info@sunrisedental.lk</p>
        <p style="margin-top: 0.5rem; font-weight: 600; color: var(--primary);">INVOICE</p>
    </div>

    <div class="print-details">
        <div>
            <h3>Bill Information</h3>
            <p><strong>Bill #:</strong> <c:out value="${bill.billNumber}" /></p>
            <p><strong>Date:</strong> <c:out value="${bill.createdAtDisplay}" /></p>
            <p><strong>Status:</strong>
                <span class="badge badge-${fn:toLowerCase(bill.paymentStatus != null ? bill.paymentStatus : 'unpaid')}">
                    <c:out value="${bill.paymentStatus}" />
                </span>
            </p>
        </div>
        <div>
            <h3>Patient Information</h3>
            <p><strong>Name:</strong> <c:out value="${bill.patientName}" /></p>
            <p><strong>Patient #:</strong> <c:out value="${bill.patientNumber}" /></p>
            <p><strong>Appointment #:</strong> <c:out value="${bill.appointmentNumber}" /></p>
        </div>
    </div>

    <table class="print-table">
        <thead>
            <tr>
                <th>#</th>
                <th>Treatment / Service</th>
                <th>Qty</th>
                <th>Unit Price (Rs.)</th>
                <th>Amount (Rs.)</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="detail" items="${billDetails}" varStatus="status">
                <tr>
                    <td><c:out value="${status.index + 1}" /></td>
                    <td><c:out value="${detail.treatmentName}" /></td>
                    <td><c:out value="${detail.quantity}" /></td>
                    <td><fmt:formatNumber value="${detail.unitPrice}" pattern="#,##0.00" /></td>
                    <td><fmt:formatNumber value="${detail.lineTotal}" pattern="#,##0.00" /></td>
                </tr>
            </c:forEach>
            <c:if test="${empty billDetails}">
                <tr>
                    <td colspan="5" class="text-center text-muted">No line items</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <div style="max-width: 300px; margin-left: auto;">
        <p style="display: flex; justify-content: space-between; padding: 0.35rem 0;">
            <span>Subtotal:</span>
            <span>Rs. <fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00" /></span>
        </p>
        <p style="display: flex; justify-content: space-between; padding: 0.35rem 0;">
            <span>Discount:</span>
            <span>Rs. <fmt:formatNumber value="${bill.discount}" pattern="#,##0.00" /></span>
        </p>
        <div class="print-total">
            Total: Rs. <fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00" />
        </div>
    </div>

    <p class="text-center text-muted mt-2" style="font-size: 0.85rem;">
        Thank you for choosing Sunrise Dental Clinic. We wish you a healthy smile!
    </p>

    <div class="print-actions no-print">
        <button type="button" class="btn btn-primary" onclick="window.print()">🖨 Print Bill</button>
        <a href="${ctx}/bills" class="btn btn-outline">Back to Bills</a>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>