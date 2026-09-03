<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page Not Found | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="error-page">
    <div class="error-content">
        <img src="${ctx}/assets/images/dental-tools.jpg" alt="Dental tools" class="error-image">
        <div class="error-code">404</div>
        <h1>Page Not Found</h1>
        <p>Sorry, the page you are looking for doesn't exist or has been moved. Let's get you back on track.</p>
        <div class="btn-group" style="justify-content: center;">
            <a href="${ctx}/dashboard" class="btn btn-primary">Go to Dashboard</a>
            <a href="${ctx}/help" class="btn btn-outline">Get Help</a>
        </div>
    </div>
</div>
</body>
</html>
