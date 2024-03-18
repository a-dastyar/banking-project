package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.model.Role;
import com.campus.banking.service.UserService;
import com.campus.banking.util.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpMethodConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/users")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class UserServlet extends HttpServlet {

    private UserService service;

    @Inject
    public void initialize(UserService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var page = ServletUtils.getPageNumber(req.getParameter("page"));
        var userList = service.getAll(page);
        req.setAttribute("users", userList);
        req.setAttribute("roles", Role.values());
        req.getRequestDispatcher("/views/pages/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var user = UserService.toUser(req.getParameterMap());
        this.service.add(user);
        resp.sendRedirect(req.getContextPath() + "/users/details?username=" + user.getUsername());
    }
}