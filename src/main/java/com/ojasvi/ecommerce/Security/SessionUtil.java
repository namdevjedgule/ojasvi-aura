package com.ojasvi.ecommerce.Security;

import com.ojasvi.ecommerce.Entity.User;

import jakarta.servlet.http.HttpSession;

public class SessionUtil {

    private SessionUtil() {
    }

    public static User getLoggedInUser(HttpSession session) {

        Object obj = session.getAttribute("user");

        if (obj instanceof User) {
            return (User) obj;
        }

        return null;
    }

    public static void logout(HttpSession session) {
        session.invalidate();
    }
}