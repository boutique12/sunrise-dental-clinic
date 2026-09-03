package com.sunrise.dental.servlet;

import com.sunrise.dental.config.ServiceFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;

public abstract class BaseServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    protected <T> T getService(String attributeName, Class<T> type) {
        ServletContext context = getServletContext();
        Object service = context.getAttribute(attributeName);
        if (service == null) {
            ServiceFactory.initialize(context);
            service = context.getAttribute(attributeName);
        }
        return type.cast(service);
    }
}
