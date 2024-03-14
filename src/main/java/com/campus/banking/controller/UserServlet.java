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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private UserService service;

    @Inject
    public void initialize(UserService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var page = Optional.ofNullable(req.getParameter("page"))
                .filter(str -> str.chars().allMatch(Character::isDigit))
                .map(Integer::valueOf)
                .filter(i -> i > 0)
                .orElse(1);
        var userList = service.getAll(page);
        req.setAttribute("users", userList);
        req.getRequestDispatcher("/views/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        this.service.addUser(UserService.toUser(req.getParameterMap()));
        resp.sendRedirect(req.getContextPath() + "/users");
    }
}