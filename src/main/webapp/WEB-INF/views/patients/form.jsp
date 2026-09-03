<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${patient != null ? 'Edit Patient' : 'New Patient'}" scope="request" />
<c:set var="activeNav" value="patients" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span>
        <a href="${ctx}/patients">Patients</a> <span>/</span>
        <span><c:out value="${patient != null ? 'Edit' : 'New'}" /></span>
    </nav>
    <h1><c:out value="${patient != null ? 'Edit Patient' : 'Register New Patient'}" /></h1>
    <p><c:out value="${patient != null ? 'Update patient information' : 'Add a new patient to Sunrise Dental Clinic'}" /></p>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-body">
        <form action="${ctx}/patients${patient != null ? '/update' : ''}" method="post">
            <c:if test="${patient != null}">
                <input type="hidden" name="patientId" value="<c:out value='${patient.patientId}' />">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="firstName">First Name <span class="required">*</span></label>
                    <input type="text" id="firstName" name="firstName" class="form-control" required
                           value="<c:out value='${patient != null ? patient.firstName : param.firstName}' />">
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name <span class="required">*</span></label>
                    <input type="text" id="lastName" name="lastName" class="form-control" required
                           value="<c:out value='${patient != null ? patient.lastName : param.lastName}' />">
                </div>
                <div class="form-group">
                    <label for="dateOfBirth">Date of Birth <span class="required">*</span></label>
                    <input type="date" id="dateOfBirth" name="dateOfBirth" class="form-control" required
                           value="<c:out value='${patient != null ? patient.dateOfBirth : param.dateOfBirth}' />">
                </div>
                <div class="form-group">
                    <label for="gender">Gender <span class="required">*</span></label>
                    <select id="gender" name="gender" class="form-control" required>
                        <option value="">Select gender</option>
                        <option value="MALE" ${(patient != null ? patient.gender : param.gender) == 'MALE' ? 'selected' : ''}>Male</option>
                        <option value="FEMALE" ${(patient != null ? patient.gender : param.gender) == 'FEMALE' ? 'selected' : ''}>Female</option>
                        <option value="OTHER" ${(patient != null ? patient.gender : param.gender) == 'OTHER' ? 'selected' : ''}>Other</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="nicNumber">NIC Number</label>
                    <input type="text" id="nicNumber" name="nicNumber" class="form-control"
                           value="<c:out value='${patient != null ? patient.nicNumber : param.nicNumber}' />">
                </div>
                <div class="form-group">
                    <label for="phone">Phone <span class="required">*</span></label>
                    <input type="tel" id="phone" name="phone" class="form-control" required
                           value="<c:out value='${patient != null ? patient.phone : param.phone}' />">
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" class="form-control"
                           value="<c:out value='${patient != null ? patient.email : param.email}' />">
                </div>
                <div class="form-group full-width">
                    <label for="address">Address</label>
                    <input type="text" id="address" name="address" class="form-control"
                           value="<c:out value='${patient != null ? patient.address : param.address}' />">
                </div>
                <div class="form-group full-width">
                    <label for="medicalNotes">Medical Notes</label>
                    <textarea id="medicalNotes" name="medicalNotes" class="form-control" rows="4"
                              placeholder="Allergies, medical conditions, previous dental history..."><c:out value="${patient != null ? patient.medicalNotes : param.medicalNotes}" /></textarea>
                </div>
                <c:if test="${patient != null}">
                    <div class="form-group">
                        <label for="active">Status</label>
                        <select id="active" name="active" class="form-control">
                            <option value="true" ${patient.active ? 'selected' : ''}>Active</option>
                            <option value="false" ${!patient.active ? 'selected' : ''}>Inactive</option>
                        </select>
                    </div>
                </c:if>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <c:out value="${patient != null ? 'Update Patient' : 'Register Patient'}" />
                </button>
                <a href="${ctx}/patients" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="../layout/footer.jspf" %>
