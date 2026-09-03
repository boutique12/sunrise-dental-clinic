<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Record Payment" scope="request" />
<c:set var="activeNav" value="payments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span>
        <a href="${ctx}/payments">Payments</a> <span>/</span>
        <span>Record</span>
    </nav>
    <h1>Record Payment</h1>
    <p>Record a payment against an outstanding bill</p>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<c:if test="${not empty selectedBill}">
    <div class="alert alert-info">
        <span>ℹ</span>
        Bill <strong><c:out value="${selectedBill.billNumber}" /></strong> —
        Patient: <c:out value="${selectedBill.patientName}" /> —
        Outstanding: Rs. <fmt:formatNumber value="${selectedBill.outstandingAmount}" pattern="#,##0.00" />
    </div>
</c:if>

<div class="card">
    <div class="card-body">
        <form action="${ctx}/payments/create" method="post">
            <div class="form-grid">
                <div class="form-group">
                    <label for="billId">Bill <span class="required">*</span></label>
                    <select id="billId" name="billId" class="form-control" required>
                        <option value="">Select unpaid bill</option>
                        <c:forEach var="b" items="${unpaidBills}">
                            <option value="${b.billId}"
                                data-outstanding="${b.outstandingAmount}"
                                ${(selectedBill != null ? selectedBill.billId : param.billId) == b.billId ? 'selected' : ''}>
                                <c:out value="${b.billNumber}" /> — <c:out value="${b.patientName}" />
                                (Rs. <fmt:formatNumber value="${b.outstandingAmount}" pattern="#,##0.00" /> outstanding)
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="amount">Amount (Rs.) <span class="required">*</span></label>
                    <input type="number" id="amount" name="amount" class="form-control" step="0.01" min="0.01" required
                           value="<c:out value='${param.amount}' />">
                </div>
                <div class="form-group">
                    <label for="paymentMethod">Payment Method <span class="required">*</span></label>
                    <select id="paymentMethod" name="paymentMethod" class="form-control" required>
                        <option value="">Select method</option>
                        <option value="CASH" ${param.paymentMethod == 'CASH' ? 'selected' : ''}>Cash</option>
                        <option value="CARD" ${param.paymentMethod == 'CARD' ? 'selected' : ''}>Card</option>
                        <option value="BANK_TRANSFER" ${param.paymentMethod == 'BANK_TRANSFER' ? 'selected' : ''}>Bank Transfer</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="referenceNumber">Reference Number</label>
                    <input type="text" id="referenceNumber" name="referenceNumber" class="form-control"
                           placeholder="Transaction or receipt reference"
                           value="<c:out value='${param.referenceNumber}' />">
                </div>
                <div class="form-group full-width">
                    <label for="notes">Notes</label>
                    <textarea id="notes" name="notes" class="form-control" rows="2"
                              placeholder="Optional payment notes"><c:out value="${param.notes}" /></textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Record Payment</button>
                <a href="${ctx}/payments" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    (function() {
        var billSelect = document.getElementById('billId');
        var amountInput = document.getElementById('amount');
        if (!billSelect || !amountInput) {
            return;
        }
        function syncAmountFromBill() {
            var selected = billSelect.options[billSelect.selectedIndex];
            if (!selected || !selected.value) {
                return;
            }
            var outstanding = selected.getAttribute('data-outstanding');
            if (outstanding && !amountInput.value) {
                amountInput.value = outstanding;
            }
        }
        billSelect.addEventListener('change', function() {
            var selected = billSelect.options[billSelect.selectedIndex];
            var outstanding = selected ? selected.getAttribute('data-outstanding') : null;
            amountInput.value = outstanding || '';
        });
        syncAmountFromBill();
    })();
</script>

<%@ include file="../layout/footer.jspf" %>