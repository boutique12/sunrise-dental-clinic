<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error | Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<div class="error-page">
    <div class="error-content">
        <img src="${ctx}/assets/images/dental-hero.jpg" alt="Sunrise Dental Clinic" class="error-image">
        <div class="error-code">500</div>
        <h1>Internal Server Error</h1>
        <p>Something went wrong on our end. Please try again later or contact support if the problem persists.</p>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error" style="text-align: left; margin-bottom: 1.5rem;">
                <span>⚠</span> <c:out value="${errorMessage}" />
            </div>
        </c:if>
        <div class="btn-group" style="justify-content: center;">
            <a href="${ctx}/dashboard" class="btn btn-primary">Go to Dashboard</a>
            <a href="javascript:history.back()" class="btn btn-outline">Go Back</a>
        </div>
    </div>
</div>
</body>
</html>
