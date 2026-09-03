<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Payment Details" scope="request" />
<c:set var="activeNav" value="payments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span>
        <a href="${ctx}/payments">Payments</a> <span>/</span>
        <span>View</span>
    </nav>
    <h1>Payment Details</h1>
    <p>Receipt information for this payment</p>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-body">
        <div class="form-grid">
            <div class="form-group">
                <label>Payment #</label>
                <p><strong><c:out value="${payment.paymentNumber}" /></strong></p>
            </div>
            <div class="form-group">
                <label>Date</label>
                <p><c:out value="${payment.paymentDateDisplay}" /></p>
            </div>
            <div class="form-group">
                <label>Bill #</label>
                <p><c:out value="${payment.billNumber}" /></p>
            </div>
            <div class="form-group">
                <label>Patient</label>
                <p><c:out value="${payment.patientName}" /></p>
            </div>
            <div class="form-group">
                <label>Amount</label>
                <p>Rs. <fmt:formatNumber value="${payment.amount}" pattern="#,##0.00" /></p>
            </div>
            <div class="form-group">
                <label>Payment Method</label>
                <p><c:out value="${payment.paymentMethod}" /></p>
            </div>
            <div class="form-group">
                <label>Received By</label>
                <p><c:out value="${payment.receivedByName}" /></p>
            </div>
            <div class="form-group">
                <label>Reference Number</label>
                <p><c:out value="${empty payment.referenceNumber ? '—' : payment.referenceNumber}" /></p>
            </div>
            <div class="form-group full-width">
                <label>Notes</label>
                <p><c:out value="${empty payment.notes ? '—' : payment.notes}" /></p>
            </div>
        </div>

        <div class="form-actions">
            <a href="${ctx}/payments" class="btn btn-outline">Back to Payments</a>
            <a href="${ctx}/bills/print?id=${billId}" class="btn btn-primary">View Bill</a>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>