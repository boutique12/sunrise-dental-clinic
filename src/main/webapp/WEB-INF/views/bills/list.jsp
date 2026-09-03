<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Bills" scope="request" />
<c:set var="activeNav" value="bills" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header flex-between">
    <div>
        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Bills</span>
        </nav>
        <h1>Billing</h1>
        <p>Manage patient bills and payment status</p>
    </div>
    <a href="${ctx}/bills/new" class="btn btn-primary">+ Create Bill</a>
</div>

<c:if test="${not empty message}">
    <div class="alert alert-success"><span>✓</span> <c:out value="${message}" /></div>
</c:if>

<div class="card">
    <div class="card-header">
        <h2>All Bills</h2>
        <form action="${ctx}/bills" method="get" class="filter-bar" style="margin-bottom: 0;">
            <div class="form-group">
                <select name="paymentStatus" class="form-control">
                    <option value="">All Payment Status</option>
                    <option value="UNPAID" ${param.paymentStatus == 'UNPAID' ? 'selected' : ''}>Unpaid</option>
                    <option value="PARTIALLY_PAID" ${param.paymentStatus == 'PARTIALLY_PAID' ? 'selected' : ''}>Partially Paid</option>
                    <option value="PAID" ${param.paymentStatus == 'PAID' ? 'selected' : ''}>Paid</option>
                </select>
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">Filter</button>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty bills}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Bill #</th>
                                <th>Patient</th>
                                <th>Appointment</th>
                                <th>Subtotal</th>
                                <th>Discount</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="bill" items="${bills}">
                                <tr>
                                    <td><c:out value="${bill.billNumber}" /></td>
                                    <td><c:out value="${bill.patientName}" /></td>
                                    <td><c:out value="${bill.appointmentNumber}" /></td>
                                    <td>Rs. <fmt:formatNumber value="${bill.subtotal}" pattern="#,##0.00" /></td>
                                    <td>Rs. <fmt:formatNumber value="${bill.discount}" pattern="#,##0.00" /></td>
                                    <td>Rs. <fmt:formatNumber value="${bill.totalAmount}" pattern="#,##0.00" /></td>
                                    <td>
                                        <span class="badge badge-${fn:toLowerCase(bill.paymentStatus)}">
                                            <c:out value="${bill.paymentStatus}" />
                                        </span>
                                    </td>
                                    <td>
                                        <div class="table-actions">
                                            <a href="${ctx}/bills/print?id=${bill.billId}" class="btn btn-sm btn-secondary">Print</a>
                                            <a href="${ctx}/bills/edit?id=${bill.billId}" class="btn btn-sm btn-outline">Edit</a>
                                            <c:if test="${bill.paymentStatus != 'PAID'}">
                                                <a href="${ctx}/payments/new?billId=${bill.billId}" class="btn btn-sm btn-primary">Pay</a>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">🧾</div>
                    <p>No bills found. Create a bill after a completed treatment.</p>
                    <a href="${ctx}/bills/new" class="btn btn-primary mt-2">+ Create Bill</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
