<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Dashboard" scope="request" />
<c:set var="activeNav" value="dashboard" scope="request" />
<%@ include file="layout/header.jspf" %>

<div class="welcome-banner">
    <div>
        <h2>Welcome, <c:out value="${sessionScope.user.fullName}" />!</h2>
        <p>
            <c:choose>
                <c:when test="${sessionScope.user.role == 'DENTIST'}">
                    You have <c:out value="${todayAppointmentCount != null ? todayAppointmentCount : 0}" /> appointments scheduled for today.
                </c:when>
                <c:otherwise>
                    Manage patients, appointments, billing, and payments from your dashboard.
                </c:otherwise>
            </c:choose>
        </p>
    </div>
    <img src="${ctx}/assets/images/dental-clinic.jpg" alt="Sunrise Dental Clinic" class="welcome-image">
</div>

<div class="stats-grid">
    <div class="stat-card">
        <div class="stat-icon purple">👥</div>
        <div class="stat-info">
            <h3><c:out value="${totalPatientCount != null ? totalPatientCount : 0}" /></h3>
            <p>Total Patients</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon blue">📅</div>
        <div class="stat-info">
            <h3><c:out value="${todayAppointmentCount != null ? todayAppointmentCount : 0}" /></h3>
            <p>Today's Appointments</p>
        </div>
    </div>
    <div class="stat-card">
        <div class="stat-icon green">✅</div>
        <div class="stat-info">
            <h3><c:out value="${completedTodayCount != null ? completedTodayCount : 0}" /></h3>
            <p>Completed Today</p>
        </div>
    </div>
    <c:if test="${sessionScope.user.role == 'RECEPTIONIST'}">
        <div class="stat-card">
            <div class="stat-icon amber">💰</div>
            <div class="stat-info">
                <h3>Rs. <fmt:formatNumber value="${todayRevenue != null ? todayRevenue : 0}" pattern="#,##0" /></h3>
                <p>Today's Revenue</p>
            </div>
        </div>
    </c:if>
</div>

<h2 class="section-title">Quick Actions</h2>
<div class="quick-links">
    <a href="${ctx}/patients/new" class="quick-link">
        <div class="quick-link-icon">👤</div>
        <span>New Patient</span>
    </a>
    <a href="${ctx}/appointments/new" class="quick-link">
        <div class="quick-link-icon">📅</div>
        <span>Book Appointment</span>
    </a>
    <a href="${ctx}/patients" class="quick-link">
        <div class="quick-link-icon">📋</div>
        <span>View Patients</span>
    </a>
    <a href="${ctx}/appointments" class="quick-link">
        <div class="quick-link-icon">🗓️</div>
        <span>View Appointments</span>
    </a>
    <c:if test="${sessionScope.user.role == 'DENTIST'}">
        <a href="${ctx}/treatments/new" class="quick-link">
            <div class="quick-link-icon">🦷</div>
            <span>Record Treatment</span>
        </a>
        <a href="${ctx}/treatments" class="quick-link">
            <div class="quick-link-icon">📝</div>
            <span>Treatment History</span>
        </a>
    </c:if>
    <c:if test="${sessionScope.user.role == 'RECEPTIONIST'}">
        <a href="${ctx}/bills/new" class="quick-link">
            <div class="quick-link-icon">🧾</div>
            <span>Create Bill</span>
        </a>
        <a href="${ctx}/payments/new" class="quick-link">
            <div class="quick-link-icon">💳</div>
            <span>Record Payment</span>
        </a>
        <a href="${ctx}/reports" class="quick-link">
            <div class="quick-link-icon">📊</div>
            <span>View Reports</span>
        </a>
    </c:if>
</div>

<div class="dashboard-grid">
    <div class="card">
        <div class="card-header">
            <h2>Upcoming Appointments</h2>
            <a href="${ctx}/appointments" class="btn btn-sm btn-secondary">View All</a>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty upcomingAppointments}">
                    <div class="table-responsive">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Date</th>
                                    <th>Time</th>
                                    <th>Patient</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="appt" items="${upcomingAppointments}">
                                    <tr>
                                        <td><c:out value="${appt.appointmentDate}" /></td>
                                        <td><c:out value="${appt.appointmentTimeDisplay}" /></td>
                                        <td><c:out value="${appt.patientName}" /></td>
                                        <td><span class="badge badge-${fn:toLowerCase(appt.status)}"><c:out value="${appt.status}" /></span></td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <div class="empty-state-icon">📅</div>
                        <p>No upcoming appointments scheduled.</p>
                        <a href="${ctx}/appointments/new" class="btn btn-primary mt-2">Book Appointment</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="card">
        <div class="card-header">
            <h2>Clinic Info</h2>
        </div>
        <div class="card-body">
            <img src="${ctx}/assets/images/dental-tools.jpg" alt="Dental tools" style="border-radius: 8px; margin-bottom: 1rem;">
            <p class="text-muted" style="font-size: 0.9rem;">
                <strong>Sunrise Dental Clinic</strong> provides comprehensive dental services including consultations,
                cleanings, fillings, extractions, and root canal treatments.
            </p>
            <p class="text-muted mt-1" style="font-size: 0.85rem;">
                <strong>Hours:</strong> Mon–Sat 8:30 AM – 4:30 PM<br>
                <strong>Phone:</strong> 011 234 5678
            </p>
        </div>
    </div>
</div>

<%@ include file="layout/footer.jspf" %>