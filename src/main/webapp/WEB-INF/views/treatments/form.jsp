<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="editMode" value="${treatment != null && treatment.treatmentId != null}" />
<c:set var="pageTitle" value="${editMode ? 'Edit Treatment' : 'Record Treatment'}" scope="request" />
<c:set var="activeNav" value="treatments" scope="request" />
<%@ include file="../layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span>
        <a href="${ctx}/treatments">Treatments</a> <span>/</span>
        <span><c:out value="${editMode ? 'Edit' : 'Record'}" /></span>
    </nav>
    <h1><c:out value="${editMode ? 'Edit Treatment Record' : 'Record New Treatment'}" /></h1>
    <p>Document diagnosis, treatment notes, and prescribed procedures</p>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-error"><span>⚠</span> <c:out value="${error}" /></div>
</c:if>

<div class="card">
    <div class="card-body">
        <form action="${ctx}/treatments${editMode ? '/update' : '/create'}" method="post">
            <c:if test="${editMode}">
                <input type="hidden" name="treatmentId" value="${treatment.treatmentId}">
            </c:if>

            <div class="form-grid">
                <div class="form-group">
                    <label for="appointmentId">Appointment <span class="required">*</span></label>
                    <select id="appointmentId" name="appointmentId" class="form-control" required
                            ${editMode ? 'disabled' : ''}>
                        <option value="">Select appointment</option>
                        <c:forEach var="appt" items="${appointments}">
                            <option value="${appt.appointmentId}"
                                ${(editMode ? treatment.appointmentId : (param.appointmentId != null ? param.appointmentId : selectedAppointmentId)) == appt.appointmentId ? 'selected' : ''}>
                                <c:out value="${appt.appointmentNumber}" /> — <c:out value="${appt.patientName}" />
                                (<c:out value="${appt.appointmentDate}" /> <c:out value="${appt.appointmentTimeDisplay}" />)
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${editMode}">
                        <input type="hidden" name="appointmentId" value="${treatment.appointmentId}">
                    </c:if>
                    <c:if test="${!editMode && empty appointments}">
                        <p class="form-hint" style="color:#b42318; margin-top:0.5rem;">
                            No scheduled appointments available. Ask reception to book an appointment first.
                        </p>
                    </c:if>
                </div>
                <div class="form-group">
                    <label for="treatmentDate">Treatment Date <span class="required">*</span></label>
                    <input type="date" id="treatmentDate" name="treatmentDate" class="form-control" required
                           value="<c:out value='${editMode ? treatment.treatmentDate : (defaultTreatmentDate != null ? defaultTreatmentDate : param.treatmentDate)}' />">
                </div>
                <div class="form-group full-width">
                    <label for="diagnosis">Diagnosis</label>
                    <input type="text" id="diagnosis" name="diagnosis" class="form-control"
                           placeholder="Clinical diagnosis"
                           value="<c:out value='${editMode ? treatment.diagnosis : param.diagnosis}' />">
                </div>
                <div class="form-group full-width">
                    <label for="treatmentNotes">Treatment Notes <span class="required">*</span></label>
                    <textarea id="treatmentNotes" name="treatmentNotes" class="form-control" rows="4" required
                              placeholder="Detailed treatment procedure and observations"><c:out value="${editMode ? treatment.treatmentNotes : param.treatmentNotes}" /></textarea>
                </div>
                <div class="form-group full-width">
                    <label for="prescription">Prescription</label>
                    <textarea id="prescription" name="prescription" class="form-control" rows="3"
                              placeholder="Medications and dosage instructions"><c:out value="${editMode ? treatment.prescription : param.prescription}" /></textarea>
                </div>
            </div>

            <h3 class="section-title mt-2">Treatment Charges</h3>
            <p class="form-hint mb-2">Select procedures performed during this treatment</p>

            <c:if test="${not empty charges}">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Select</th>
                                <th>Code</th>
                                <th>Treatment</th>
                                <th>Standard Charge (Rs.)</th>
                                <th>Quantity</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="charge" items="${charges}" varStatus="status">
                                <tr>
                                    <td>
                                        <input type="checkbox" name="chargeIds" value="${charge.chargeId}"
                                               id="charge-cb-${charge.chargeId}">
                                    </td>
                                    <td><c:out value="${charge.treatmentCode}" /></td>
                                    <td><c:out value="${charge.treatmentName}" /></td>
                                    <td><c:out value="${charge.standardCharge}" /></td>
                                    <td>
                                        <input type="number" name="quantity_${charge.chargeId}" id="charge-qty-${charge.chargeId}"
                                               class="form-control" min="1" value="1"
                                               style="width: 80px;" disabled>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <c:if test="${empty charges}">
                <div class="alert alert-error"><span>⚠</span> No treatment charges found in the system.</div>
            </c:if>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <c:out value="${editMode ? 'Update Treatment' : 'Save Treatment'}" />
                </button>
                <a href="${ctx}/treatments" class="btn btn-outline">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    (function() {
        document.querySelectorAll('input[name="chargeIds"]').forEach(function(checkbox) {
            var quantityInput = document.getElementById('charge-qty-' + checkbox.value);
            if (!quantityInput) {
                return;
            }
            function syncQuantityState() {
                quantityInput.disabled = !checkbox.checked;
                if (!checkbox.checked) {
                    quantityInput.value = '1';
                }
            }
            checkbox.addEventListener('change', syncQuantityState);
            syncQuantityState();
        });
    })();
</script>

<%@ include file="../layout/footer.jspf" %>