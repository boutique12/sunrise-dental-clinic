package com.sunrise.dental.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class ServletUtil {

    private ServletUtil() {
    }

    public static void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    public static String pathAction(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.isBlank() || "/".equals(pathInfo)) {
            return "list";
        }
        String action = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
        int slash = action.indexOf('/');
        if (slash >= 0) {
            action = action.substring(0, slash);
        }
        return action.isBlank() ? "list" : action;
    }

    public static void applyFlashMessage(HttpServletRequest request) {
        String message = request.getParameter("message");
        if (message != null && !message.isBlank()) {
            request.setAttribute("message", URLDecoder.decode(message, StandardCharsets.UTF_8));
        }
    }

    public static void applyError(HttpServletRequest request) {
        Object errorMessage = request.getAttribute("errorMessage");
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
        }
    }

    public static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
