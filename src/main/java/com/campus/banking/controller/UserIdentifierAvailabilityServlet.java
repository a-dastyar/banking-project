package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.service.UserService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/users/available")
public class UserIdentifierAvailabilityServlet extends HttpServlet {

    private UserService service;

    @Inject
    public void initialize(UserService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var username = Optional.ofNullable(req.getParameter("username"));
        var email = Optional.ofNullable(req.getParameter("email"));
        var isUsernameAvailable = username.map(service::isUsernameAvailable).orElse(null);
        var isEmailAvailable = email.map(service::isEmailAvailable).orElse(null);
        var result = """
                {
                    "username": %s,
                    "email": %s
                }
                """.formatted(isUsernameAvailable, isEmailAvailable);
        resp.setContentType("application/json");
        resp.getWriter().print(result);
    }
}
