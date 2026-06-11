package com.ojasvi.ecommerce.Controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ojasvi.ecommerce.Entity.User;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalUserController {

    @ModelAttribute("user")
    public User addUserToModel(HttpSession session) {
        Object user = session.getAttribute("user");
        return user != null ? (User) user : null;
    }
}
