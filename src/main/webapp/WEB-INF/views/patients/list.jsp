<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Patients" scope="request" />
<c:set var="activeNav" value="patients" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header flex-between">
    <div>
        <nav class="breadcrumb">
            <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Patients</span>
        </nav>
        <h1>Patient Records</h1>
        <p>Manage and search all registered patients at Sunrise Dental Clinic</p>
    </div>
    <a href="${ctx}/patients/new" class="btn btn-primary">+ New Patient</a>
</div>

<c:if test="${not empty message}">
    <div class="alert alert-success"><span>✓</span> <c:out value="${message}" /></div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-header">
        <h2>All Patients</h2>
        <form action="${ctx}/patients" method="get" class="filter-bar" style="margin-bottom: 0;">
            <div class="form-group">
                <input type="text" name="search" class="form-control" placeholder="Search by name, phone, or patient number..."
                       value="<c:out value='${param.search}' />">
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">Search</button>
        </form>
    </div>
    <div class="card-body">
        <c:choose>
            <c:when test="${not empty patients}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Patient #</th>
                                <th>Name</th>
                                <th>Date of Birth</th>
                                <th>Gender</th>
                                <th>Phone</th>
                                <th>Email</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="patient" items="${patients}">
                                <tr>
                                    <td><c:out value="${patient.patientNumber}" /></td>
                                    <td><c:out value="${patient.firstName}" /> <c:out value="${patient.lastName}" /></td>
                                    <td><c:out value="${patient.dateOfBirth}" /></td>
                                    <td><c:out value="${patient.gender}" /></td>
                                    <td><c:out value="${patient.phone}" /></td>
                                    <td><c:out value="${patient.email != null ? patient.email : '—'}" /></td>
                                    <td>
                                        <span class="badge ${patient.active ? 'badge-active' : 'badge-inactive'}">
                                            <c:out value="${patient.active ? 'Active' : 'Inactive'}" />
                                        </span>
                                    </td>
                                    <td>
                                        <div class="table-actions">
                                            <a href="${ctx}/patients/edit?id=${patient.patientId}" class="btn btn-sm btn-secondary">Edit</a>
                                            <a href="${ctx}/appointments?patientId=${patient.patientId}" class="btn btn-sm btn-outline">Appointments</a>
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
                    <div class="empty-state-icon">👥</div>
                    <p>No patients found. Register your first patient to get started.</p>
                    <a href="${ctx}/patients/new" class="btn btn-primary mt-2">+ New Patient</a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
