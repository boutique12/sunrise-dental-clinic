<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="editMode" value="${appointment != null && appointment.appointmentId != null}" />
<c:set var="pageTitle" value="${editMode ? 'Edit Appointment' : 'Book Appointment'}" scope="request" />
<c:set var="activeNav" value="appointments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span>
        <a href="${ctx}/appointments">Appointments</a> <span>/</span>
        <span><c:out value="${editMode ? 'Edit' : 'Book'}" /></span>
    </nav>
    <h1><c:out value="${editMode ? 'Edit Appointment' : 'Book New Appointment'}" /></h1>
    <p>Clinic hours: Mon–Sat, 8:30 AM – 5:00 PM (30-minute appointment slots)</p>
</div>

<div class="dentist-team-banner">
    <h3>Our Dental Team</h3>
    <div class="dentist-team-grid">
        <c:forEach var="d" items="${dentists}">
            <div class="dentist-chip">
                <span class="dentist-chip-icon">🦷</span>
                <span class="dentist-chip-name"><c:out value="${d.fullName}" /></span>
            </div>
        </c:forEach>
    </div>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-body">
        <form action="${ctx}/appointments${editMode ? '/update' : ''}" method="post">
            <c:if test="${editMode}">
                <input type="hidden" name="appointmentId" value="${appointment.appointmentId}">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="patientId">Patient <span class="required">*</span></label>
                    <select id="patientId" name="patientId" class="form-control" required>
                        <option value="">Select patient</option>
                        <c:forEach var="p" items="${patients}">
                            <option value="${p.patientId}"
                                ${(editMode ? appointment.patientId : param.patientId) == p.patientId ? 'selected' : ''}>
                                <c:out value="${p.patientNumber}" /> — <c:out value="${p.firstName}" /> <c:out value="${p.lastName}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="dentistId">Dentist <span class="required">*</span></label>
                    <select id="dentistId" name="dentistId" class="form-control" required>
                        <option value="">Select dentist</option>
                        <c:forEach var="d" items="${dentists}">
                            <option value="${d.userId}"
                                ${(editMode ? appointment.dentistId : param.dentistId) == d.userId ? 'selected' : ''}>
                                <c:out value="${d.fullName}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="appointmentDate">Date <span class="required">*</span></label>
                    <input type="date" id="appointmentDate" name="appointmentDate" class="form-control" required
                           value="<c:out value='${editMode ? appointment.appointmentDate : param.appointmentDate}' />">
                </div>
                <div class="form-group">
                    <label for="appointmentTime">Time Slot <span class="required">*</span></label>
                    <select id="appointmentTime" name="appointmentTime" class="form-control" required>
                        <option value="">Select time (8:30 AM – 5:00 PM)</option>
                        <c:forEach var="slot" items="${timeSlotOptions}">
                            <option value="${slot.value}"
                                ${(editMode ? appointment.appointmentTime.toString() : param.appointmentTime) == slot.value ? 'selected' : ''}>
                                <c:out value="${slot.label}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group full-width">
                    <label for="reason">Reason for Visit <span class="required">*</span></label>
                    <input type="text" id="reason" name="reason" class="form-control" required
                           placeholder="e.g. Dental checkup, tooth pain, cleaning"
                           value="<c:out value='${editMode ? appointment.reason : param.reason}' />">
                </div>
                <c:if test="${editMode}">
                    <div class="form-group">
                        <label for="status">Status</label>
                        <select id="status" name="status" class="form-control">
                            <option value="SCHEDULED" ${appointment.status == 'SCHEDULED' ? 'selected' : ''}>Scheduled</option>
                            <option value="COMPLETED" ${appointment.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="CANCELLED" ${appointment.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                            <option value="NO_SHOW" ${appointment.status == 'NO_SHOW' ? 'selected' : ''}>No Show</option>
                        </select>
                    </div>
                </c:if>
                <div class="form-group full-width">
                    <label for="notes">Notes</label>
                    <textarea id="notes" name="notes" class="form-control" rows="3"
                              placeholder="Additional notes for the appointment"><c:out value="${editMode ? appointment.notes : param.notes}" /></textarea>
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <c:out value="${editMode ? 'Update Appointment' : 'Book Appointment'}" />
                </button>
                <a href="${ctx}/appointments" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    (function() {
        var dateInput = document.getElementById('appointmentDate');
        if (dateInput && !dateInput.min) {
            dateInput.min = new Date().toISOString().split('T')[0];
        }
    })();
</script>

<%@ include file="../layout/footer.jspf" %>