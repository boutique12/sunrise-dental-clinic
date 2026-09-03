<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Treatments" scope="request" />
<c:set var="activeNav" value="treatments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header flex-between">
    <div>
        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Treatments</span>
        </nav>
        <h1>Treatment Records</h1>
        <p>View and manage dental treatment history</p>
    </div>
    <a href="${ctx}/treatments/new" class="btn btn-primary">+ Record Treatment</a>
</div>

<c:if test="${not empty message}">
    <div class="alert alert-success"><span>✓</span> <c:out value="${message}" /></div>
</c:if>

<div class="card">
    <div class="card-header">
        <h2>All Treatments</h2>
        <form action="${ctx}/treatments" method="get" class="filter-bar" style="margin-bottom: 0;">
            <div class="form-group">
                <input type="date" name="fromDate" class="form-control" placeholder="From"
                       value="<c:out value='${param.fromDate}' />">
            </div>
            <div class="form-group">
                <input type="date" name="toDate" class="form-control" placeholder="To"
                       value="<c:out value='${param.toDate}' />">
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">Filter</button>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty treatments}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Patient</th>
                                <th>Appointment #</th>
                                <th>Dentist</th>
                                <th>Diagnosis</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="treatment" items="${treatments}">
                                <tr>
                                    <td><c:out value="${treatment.treatmentDate}" /></td>
                                    <td><c:out value="${treatment.patientName}" /></td>
                                    <td><c:out value="${treatment.appointmentNumber}" /></td>
                                    <td><c:out value="${treatment.dentistName}" /></td>
                                    <td><c:out value="${treatment.diagnosis != null ? treatment.diagnosis : '—'}" /></td>
                                    <td>
                                        <div class="table-actions">
                                            <a href="${ctx}/treatments/view?id=${treatment.treatmentId}" class="btn btn-sm btn-secondary">View</a>
                                            <a href="${ctx}/treatments/edit?id=${treatment.treatmentId}" class="btn btn-sm btn-outline">Edit</a>
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
                    <div class="empty-state-icon">🦷</div>
                    <p>No treatment records found.</p>
                    <a href="${ctx}/treatments/new" class="btn btn-primary mt-2">+ Record Treatment</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
