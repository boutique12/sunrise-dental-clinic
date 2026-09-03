<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Help" scope="request" />
<c:set var="activeNav" value="help" scope="request" />
<%@ include file="layout/header.jspf" %>

<div class="page-header">
    <nav class="breadcrumb">
        <a href="${ctx}/dashboard">Dashboard</a> <span>/</span> <span>Help</span>
    </nav>
    <h1>Help & Support</h1>
    <p>Guide to using the Sunrise Dental Clinic management system</p>
</div>

<div class="dashboard-grid">
    <div>
        <div class="card help-section">
            <div class="card-header"><h2>Getting Started</h2></div>
            <div class="card-body">
                <ul class="help-list">
                    <li><strong>Login:</strong> Use your assigned username and password to access the staff portal.</li>
                    <li><strong>Dashboard:</strong> View clinic statistics, upcoming appointments, and quick action links.</li>
                    <li><strong>Navigation:</strong> Use the top menu to access Patients, Appointments, Treatments, Bills, and more.</li>
                </ul>
            </div>
        </div>

        <div class="card help-section">
            <div class="card-header"><h2>Role-Based Access</h2></div>
            <div class="card-body">
                <h3 class="section-title" style="font-size: 1rem;">Receptionist</h3>
                <ul class="help-list">
                    <li>Register and manage patient records</li>
                    <li>Book and manage appointments</li>
                    <li>Create bills and record payments</li>
                    <li>Generate financial and operational reports</li>
                </ul>
                <h3 class="section-title mt-2" style="font-size: 1rem;">Dentist</h3>
                <ul class="help-list">
                    <li>View assigned appointments</li>
                    <li>Record treatment notes and diagnoses</li>
                    <li>Select treatment charges and procedures</li>
                    <li>View treatment history</li>
                </ul>
            </div>
        </div>

        <div class="card help-section">
            <div class="card-header"><h2>Frequently Asked Questions</h2></div>
            <div class="card-body">
                <details class="faq-item">
                    <summary>How do I register a new patient?</summary>
                    <p>Navigate to Patients → New Patient, fill in the required fields (name, date of birth, gender, phone), and click Register Patient.</p>
                </details>
                <details class="faq-item">
                    <summary>How do I book an appointment?</summary>
                    <p>Go to Appointments → Book Appointment, select the patient and dentist, choose date and time, and enter the reason for visit.</p>
                </details>
                <details class="faq-item">
                    <summary>How do I create a bill?</summary>
                    <p>After a treatment is completed, go to Bills → Create Bill, select the appointment, review charges, apply any discount, and save.</p>
                </details>
                <details class="faq-item">
                    <summary>How do I record a payment?</summary>
                    <p>Go to Payments → Record Payment, select the bill, enter the amount and payment method, and submit.</p>
                </details>
                <details class="faq-item">
                    <summary>What if I forget my password?</summary>
                    <p>Contact your clinic administrator to reset your password. For demo accounts, use reception01 or dentist.amara.</p>
                </details>
            </div>
        </div>
    </div>

    <div>
        <div class="card">
            <div class="card-header"><h2>Contact Support</h2></div>
            <div class="card-body">
                <img src="${ctx}/assets/images/dental-clinic.jpg" alt="Sunrise Dental Clinic" style="border-radius: 8px; margin-bottom: 1rem;">
                <p><strong>Sunrise Dental Clinic</strong></p>
                <p class="text-muted" style="font-size: 0.9rem;">
                    123 Dental Avenue<br>
                    Colombo, Sri Lanka
                </p>
                <p class="mt-1">
                    <strong>Phone:</strong> <a href="tel:+94112345678">011 234 5678</a><br>
                    <strong>Email:</strong> <a href="mailto:support@sunrisedental.lk">support@sunrisedental.lk</a>
                </p>
                <p class="text-muted mt-2" style="font-size: 0.85rem;">
                    System support hours: Mon–Sat, 8:00 AM – 6:00 PM
                </p>
            </div>
        </div>

        <div class="card mt-2">
            <div class="card-header"><h2>Keyboard Shortcuts</h2></div>
            <div class="card-body">
                <ul class="help-list">
                    <li><strong>Alt + D</strong> — Go to Dashboard</li>
                    <li><strong>Alt + P</strong> — Go to Patients</li>
                    <li><strong>Alt + A</strong> — Go to Appointments</li>
                    <li><strong>Ctrl + P</strong> — Print (on bill pages)</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<%@ include file="layout/footer.jspf" %>
