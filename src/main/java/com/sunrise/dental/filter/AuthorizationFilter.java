package com.sunrise.dental.filter;

import com.sunrise.dental.model.User;
import com.sunrise.dental.util.AppConstants;
import com.sunrise.dental.util.ServletUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AuthorizationFilter implements Filter {

    private static final Map<String, Set<String>> PATH_ROLE_PERMISSIONS = Map.of(
            "/patients", Set.of(AppConstants.ROLE_RECEPTIONIST, AppConstants.ROLE_DENTIST),
            "/appointments", Set.of(AppConstants.ROLE_RECEPTIONIST, AppConstants.ROLE_DENTIST),
            "/treatments", Set.of(AppConstants.ROLE_DENTIST),
            "/bills", Set.of(AppConstants.ROLE_RECEPTIONIST),
            "/payments", Set.of(AppConstants.ROLE_RECEPTIONIST),
            "/reports", Set.of(AppConstants.ROLE_RECEPTIONIST),
            "/help", Set.of(AppConstants.ROLE_RECEPTIONIST, AppConstants.ROLE_DENTIST),
            "/dashboard", Set.of(AppConstants.ROLE_RECEPTIONIST, AppConstants.ROLE_DENTIST),
            "/api/appointments", Set.of(AppConstants.ROLE_RECEPTIONIST, AppConstants.ROLE_DENTIST)
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ServletUtil.setNoCacheHeaders(httpResponse);

        HttpSession session = httpRequest.getSession(false);
        User user = session != null ? (User) session.getAttribute(AppConstants.SESSION_USER) : null;
        if (user == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String path = resolveResourcePath(httpRequest);
        Set<String> allowedRoles = PATH_ROLE_PERMISSIONS.get(path);
        if (allowedRoles != null && !allowedRoles.contains(user.getRole())) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for your role");
            return;
        }

        if (isWriteOperation(httpRequest) && isReadOnlyPath(path, user.getRole())) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Write access denied for your role");
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveResourcePath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.isBlank()) {
            return servletPath;
        }
        return "/";
    }

    private boolean isWriteOperation(HttpServletRequest request) {
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)) {
            return true;
        }
        String action = ServletUtil.pathAction(request);
        return action != null && !"view".equalsIgnoreCase(action) && !"list".equalsIgnoreCase(action)
                && !"print".equalsIgnoreCase(action) && !"new".equalsIgnoreCase(action)
                && !"edit".equalsIgnoreCase(action);
    }

    private boolean isReadOnlyPath(String path, String role) {
        if (AppConstants.ROLE_DENTIST.equals(role)) {
            return "/patients".equals(path) || "/appointments".equals(path);
        }
        return false;
    }
}
