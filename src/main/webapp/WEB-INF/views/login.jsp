<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="login-page">
    <div class="login-hero">
        <div class="login-hero-content">
            <h1>Sunrise Dental Clinic</h1>
            <p>Your trusted partner for exceptional dental care. Manage appointments, patients, and treatments with ease.</p>
            <div class="login-hero-badge">
                <strong>Quality Care</strong> &bull; <strong>Modern Technology</strong> &bull; <strong>Bright Smiles</strong>
            </div>
        </div>
    </div>
    <div class="login-form-panel">
        <div class="login-form-container">
            <div class="brand">
                <div class="brand-logo">🦷</div>
                <div class="brand-text">
                    Sunrise Dental Clinic
                    <span>Staff Portal</span>
                </div>
            </div>
            <h2>Welcome Back</h2>
            <p class="subtitle">Sign in to access the clinic management system</p>

            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <span>⚠</span> <c:out value="${error}" />
                </div>
            </c:if>
            <c:if test="${not empty message}">
                <div class="alert alert-success">
                    <span>✓</span> <c:out value="${message}" />
                </div>
            </c:if>

            <form action="${ctx}/login" method="post">
                <div class="form-group">
                    <label for="username">Username <span class="required">*</span></label>
                    <input type="text" id="username" name="username" class="form-control"
                           placeholder="Enter your username" required autofocus
                           value="<c:out value='${param.username}' />">
                </div>
                <div class="form-group">
                    <label for="password">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" class="form-control"
                           placeholder="Enter your password" required>
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.85rem;">
                        Sign In
                    </button>
                </div>
            </form>

            <p class="text-center text-muted mt-2" style="font-size: 0.8rem;">
                Demo: reception01 / dentist.gayathri
            </p>
        </div>
    </div>
</div>
</body>
</html>
