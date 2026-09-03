<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Payments" scope="request" />
<c:set var="activeNav" value="payments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header flex-between">
    <div>
        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Payments</span>
        </nav>
        <h1>Payments</h1>
        <p>Track and record patient payments</p>
    </div>
    <a href="${ctx}/payments/new" class="btn btn-primary">+ Record Payment</a>
</div>

<c:if test="${not empty message}">
    <div class="alert alert-success"><span>✓</span> <c:out value="${message}" /></div>
</c:if>

<div class="card">
    <div class="card-header">
        <h2>Payment History</h2>
        <form action="${ctx}/payments" method="get" class="filter-bar" style="margin-bottom: 0;">
            <div class="form-group">
                <input type="date" name="fromDate" class="form-control"
                       value="<c:out value='${param.fromDate}' />">
            </div>
            <div class="form-group">
                <input type="date" name="toDate" class="form-control"
                       value="<c:out value='${param.toDate}' />">
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">Filter</button>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty payments}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Payment #</th>
                                <th>Date</th>
                                <th>Bill #</th>
                                <th>Patient</th>
                                <th>Amount</th>
                                <th>Method</th>
                                <th>Received By</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="payment" items="${payments}">
                                <tr>
                                    <td><c:out value="${payment.paymentNumber}" /></td>
                                    <td><c:out value="${payment.paymentDateDisplay}" /></td>
                                    <td><c:out value="${payment.billNumber}" /></td>
                                    <td><c:out value="${payment.patientName}" /></td>
                                    <td>Rs. <fmt:formatNumber value="${payment.amount}" pattern="#,##0.00" /></td>
                                    <td><c:out value="${payment.paymentMethod}" /></td>
                                    <td><c:out value="${payment.receivedByName}" /></td>
                                    <td>
                                        <a href="${ctx}/payments/view?id=${payment.paymentId}" class="btn btn-sm btn-secondary">View</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">💳</div>
                    <p>No payments recorded yet.</p>
                    <a href="${ctx}/payments/new" class="btn btn-primary mt-2">+ Record Payment</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
