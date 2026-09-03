<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${bill != null ? 'Edit Bill' : 'Create Bill'}" scope="request" />
<c:set var="activeNav" value="bills" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span>
        <a href="${ctx}/bills">Bills</a> <span>/</span>
        <span><c:out value="${bill != null ? 'Edit' : 'Create'}" /></span>
    </nav>
    <h1><c:out value="${bill != null ? 'Edit Bill' : 'Create New Bill'}" /></h1>
    <p>Generate a bill for a completed appointment</p>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-body">
        <form action="${ctx}/bills${bill != null ? '/update' : ''}" method="post">
            <c:if test="${bill != null}">
                <input type="hidden" name="billId" value="<c:out value='${bill.billId}' />">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="appointmentId">Appointment <span class="required">*</span></label>
                    <select id="appointmentId" name="appointmentId" class="form-control" required
                            ${bill != null ? 'disabled' : ''}>
                        <option value="">Select completed appointment</option>
                        <c:forEach var="appt" items="${appointments}">
                            <option value="${appt.appointmentId}"
                                ${(bill != null ? bill.appointmentId : param.appointmentId) == appt.appointmentId ? 'selected' : ''}>
                                <c:out value="${appt.appointmentNumber}" /> — <c:out value="${appt.patientName}" />
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${bill != null}">
                        <input type="hidden" name="appointmentId" value="<c:out value='${bill.appointmentId}' />">
                    </c:if>
                </div>
                <div class="form-group">
                    <label for="subtotal">Subtotal (Rs.) <span class="required">*</span></label>
                    <input type="number" id="subtotal" name="subtotal" class="form-control" step="0.01" min="0" required
                           value="<c:out value='${bill != null ? bill.subtotal : param.subtotal}' />">
                </div>
                <div class="form-group">
                    <label for="discount">Discount (Rs.)</label>
                    <input type="number" id="discount" name="discount" class="form-control" step="0.01" min="0"
                           value="<c:out value='${bill != null ? bill.discount : (param.discount != null ? param.discount : 0)}' />">
                </div>
                <c:if test="${bill != null}">
                    <div class="form-group">
                        <label for="paymentStatus">Payment Status</label>
                        <select id="paymentStatus" name="paymentStatus" class="form-control">
                            <option value="UNPAID" ${bill.paymentStatus == 'UNPAID' ? 'selected' : ''}>Unpaid</option>
                            <option value="PARTIALLY_PAID" ${bill.paymentStatus == 'PARTIALLY_PAID' ? 'selected' : ''}>Partially Paid</option>
                            <option value="PAID" ${bill.paymentStatus == 'PAID' ? 'selected' : ''}>Paid</option>
                        </select>
                    </div>
                </c:if>
            </div>

            <c:if test="${not empty billDetails}">
                <h3 class="section-title mt-2">Bill Line Items</h3>
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Treatment</th>
                                <th>Qty</th>
                                <th>Unit Price</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="detail" items="${billDetails}">
                                <tr>
                                    <td><c:out value="${detail.treatmentName}" /></td>
                                    <td><c:out value="${detail.quantity}" /></td>
                                    <td>Rs. <fmt:formatNumber value="${detail.unitPrice}" pattern="#,##0.00" /></td>
                                    <td>Rs. <fmt:formatNumber value="${detail.quantity * detail.unitPrice}" pattern="#,##0.00" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <c:out value="${bill != null ? 'Update Bill' : 'Create Bill'}" />
                </button>
                <a href="${ctx}/bills" class="btn btn-outline">Cancel</a>
                <c:if test="${bill != null}">
                    <a href="${ctx}/bills/print?id=${bill.billId}" class="btn btn-secondary">Print Bill</a>
                </c:if>
            </div>
        </form>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
