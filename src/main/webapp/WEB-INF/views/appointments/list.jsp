<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Appointments" scope="request" />
<c:set var="activeNav" value="appointments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header flex-between">
    <div>
        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Appointments</span>
        </nav>
        <h1>Appointments</h1>
        <p>Schedule and manage patient appointments</p>
    </div>
    <a href="${ctx}/appointments/new" class="btn btn-primary">+ Book Appointment</a>
</div>

<c:if test="${not empty message}">
    <div class="alert alert-success"><span>✓</span> <c:out value="${message}" /></div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-header">
        <h2>Appointment List</h2>
        <form action="${ctx}/appointments" method="get" class="filter-bar" style="margin-bottom: 0;">
            <div class="form-group">
                <input type="date" name="date" class="form-control"
                       value="<c:out value='${param.date}' />">
            </div>
            <div class="form-group">
                <select name="status" class="form-control">
                    <option value="">All Statuses</option>
                    <option value="SCHEDULED" ${param.status == 'SCHEDULED' ? 'selected' : ''}>Scheduled</option>
                    <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                    <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    <option value="NO_SHOW" ${param.status == 'NO_SHOW' ? 'selected' : ''}>No Show</option>
                </select>
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">Filter</button>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty appointments}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Appt #</th>
                                <th>Date</th>
                                <th>Time</th>
                                <th>Patient</th>
                                <th>Dentist</th>
                                <th>Reason</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="appt" items="${appointments}">
                                <tr>
                                    <td><c:out value="${appt.appointmentNumber}" /></td>
                                    <td><c:out value="${appt.appointmentDate}" /></td>
                                    <td><c:out value="${appt.appointmentTime}" /></td>
                                    <td><c:out value="${appt.patientName}" /></td>
                                    <td><c:out value="${appt.dentistName}" /></td>
                                    <td><c:out value="${appt.reason}" /></td>
                                    <td>
                                        <span class="badge badge-${fn:toLowerCase(appt.status)}">
                                            <c:out value="${appt.status}" />
                                        </span>
                                    </td>
                                    <td>
                                        <div class="table-actions">
                                            <a href="${ctx}/appointments/edit?id=${appt.appointmentId}" class="btn btn-sm btn-secondary">Edit</a>
                                            <c:if test="${appt.status == 'SCHEDULED' && sessionScope.user.role == 'DENTIST'}">
                                                <a href="${ctx}/treatments/new?appointmentId=${appt.appointmentId}" class="btn btn-sm btn-primary">Treat</a>
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
                    <div class="empty-state-icon">📅</div>
                    <p>No appointments found. Book a new appointment to get started.</p>
                    <a href="${ctx}/appointments/new" class="btn btn-primary mt-2">+ Book Appointment</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
