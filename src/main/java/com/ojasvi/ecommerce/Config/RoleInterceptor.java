package com.ojasvi.ecommerce.Config;

import com.ojasvi.ecommerce.Entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String uri = request.getRequestURI();

        User user =
                (User) request.getSession()
                        .getAttribute("user");

        // User not logged in
        if (user == null) {

            // AJAX/API requests
            if (uri.startsWith("/checkout/")
                    || uri.startsWith("/cart/")
                    || uri.startsWith("/wishlist/")) {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED);

                response.setContentType(
                        "application/json");

                response.getWriter().write("""
                        {
                          "success": false,
                          "message": "Please login"
                        }
                        """);

                return false;
            }

            response.sendRedirect("/login");
            return false;
        }

        String role =
                user.getRole().getRoleName();

        // Admin pages
        if (uri.startsWith("/admin")) {

            if (!"ADMIN".equalsIgnoreCase(role)) {
                response.sendRedirect("/access-denied");
                return false;
            }
        }

        // Customer pages
        if (uri.startsWith("/customer")) {

            if (!"CUSTOMER".equalsIgnoreCase(role)) {
                response.sendRedirect("/access-denied");
                return false;
            }
        }

        return true;
    }
}