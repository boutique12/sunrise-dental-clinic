<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Reports" scope="request" />
<c:set var="activeNav" value="reports" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Reports</span>
    </nav>
    <h1>Reports & Analytics</h1>
    <p>Generate insights and reports for Sunrise Dental Clinic</p>
</div>

<div class="report-cards">
    <div class="report-card${reportType == 'appointments' ? ' report-card-active' : ''}">
        <h3>📅 Appointment Report</h3>
        <p>View appointment statistics by date range, dentist, and status.</p>
        <form action="${ctx}/reports/appointments" method="get">
            <div class="form-group">
                <label for="apptFrom">From Date</label>
                <input type="date" id="apptFrom" name="fromDate" class="form-control" required
                       value="${reportType == 'appointments' ? reportFromDate : ''}">
            </div>
            <div class="form-group">
                <label for="apptTo">To Date</label>
                <input type="date" id="apptTo" name="toDate" class="form-control" required
                       value="${reportType == 'appointments' ? reportToDate : ''}">
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Generate Report</button>
        </form>
    </div>

    <div class="report-card${reportType == 'revenue' ? ' report-card-active' : ''}">
        <h3>💰 Revenue Report</h3>
        <p>Summarize billing and payment revenue over a selected period.</p>
        <form action="${ctx}/reports/revenue" method="get">
            <div class="form-group">
                <label for="revFrom">From Date</label>
                <input type="date" id="revFrom" name="fromDate" class="form-control" required
                       value="${reportType == 'revenue' ? reportFromDate : ''}">
            </div>
            <div class="form-group">
                <label for="revTo">To Date</label>
                <input type="date" id="revTo" name="toDate" class="form-control" required
                       value="${reportType == 'revenue' ? reportToDate : ''}">
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Generate Report</button>
        </form>
    </div>

    <div class="report-card${reportType == 'patients' ? ' report-card-active' : ''}">
        <h3>👥 Patient Report</h3>
        <p>Overview of patient registrations and demographics.</p>
        <form action="${ctx}/reports/patients" method="get">
            <div class="form-group">
                <label for="patFrom">From Date</label>
                <input type="date" id="patFrom" name="fromDate" class="form-control"
                       value="${reportType == 'patients' ? reportFromDate : ''}">
            </div>
            <div class="form-group">
                <label for="patTo">To Date</label>
                <input type="date" id="patTo" name="toDate" class="form-control"
                       value="${reportType == 'patients' ? reportToDate : ''}">
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Generate Report</button>
        </form>
    </div>

    <div class="report-card${reportType == 'treatments' ? ' report-card-active' : ''}">
        <h3>🦷 Treatment Report</h3>
        <p>Analyze treatments performed and charges by procedure type.</p>
        <form action="${ctx}/reports/treatments" method="get">
            <div class="form-group">
                <label for="treatFrom">From Date</label>
                <input type="date" id="treatFrom" name="fromDate" class="form-control" required
                       value="${reportType == 'treatments' ? reportFromDate : ''}">
            </div>
            <div class="form-group">
                <label for="treatTo">To Date</label>
                <input type="date" id="treatTo" name="toDate" class="form-control" required
                       value="${reportType == 'treatments' ? reportToDate : ''}">
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Generate Report</button>
        </form>
    </div>

    <div class="report-card${reportType == 'outstanding-bills' ? ' report-card-active' : ''}">
        <h3>🧾 Outstanding Bills</h3>
        <p>List all unpaid and partially paid bills with outstanding balances.</p>
        <a href="${ctx}/reports/outstanding-bills" class="btn btn-primary btn-sm">View Report</a>
    </div>

    <div class="report-card${reportType == 'payments' ? ' report-card-active' : ''}">
        <h3>💳 Payment Summary</h3>
        <p>Payment breakdown by method (Cash, Card, Bank Transfer).</p>
        <form action="${ctx}/reports/payments" method="get">
            <div class="form-group">
                <label for="payFrom">From Date</label>
                <input type="date" id="payFrom" name="fromDate" class="form-control" required
                       value="${reportType == 'payments' ? reportFromDate : ''}">
            </div>
            <div class="form-group">
                <label for="payTo">To Date</label>
                <input type="date" id="payTo" name="toDate" class="form-control" required
                       value="${reportType == 'payments' ? reportToDate : ''}">
            </div>
            <button type="submit" class="btn btn-primary btn-sm">Generate Report</button>
        </form>
    </div>
</div>

<c:if test="${reportGenerated}">
    <div id="report-results" class="report-results-panel">
        <div class="report-generated-banner">
            <span class="report-generated-icon">✓</span>
            <div>
                <strong>Report generated</strong>
                <p>Results for <c:out value="${reportTitle}" /> are shown below.</p>
            </div>
        </div>

        <div class="card mt-2">
            <div class="card-header report-results-header">
                <h2><c:out value="${reportTitle}" /></h2>
                <span class="report-row-count">
                    <c:choose>
                        <c:when test="${empty reportData}">0 records</c:when>
                        <c:otherwise>${reportData.size()} record<c:if test="${reportData.size() != 1}">s</c:if></c:otherwise>
                    </c:choose>
                </span>
            </div>
            <div class="card-body">
                <c:choose>
                    <c:when test="${empty reportData}">
                        <div class="empty-state report-empty-state">
                            <p>No records found for the selected date range.</p>
                            <p class="text-muted">Try widening the date range or check that data exists for this period.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                       <c:forEach var="columnHeader" items="${reportHeaders}">
    <th><c:out value="${columnHeader}" /></th>
</c:forEach>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="row" items="${reportData}">
                                        <tr>
                                            <c:forEach var="cell" items="${row}">
                                                <td><c:out value="${cell}" /></td>
                                            </c:forEach>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script>
        (function () {
            var results = document.getElementById('report-results');
            if (results) {
                results.scrollIntoView({ behavior: 'smooth', block: 'start' });
            }
        })();
    </script>
</c:if>

<%@ include file="../layout/footer.jspf" %>