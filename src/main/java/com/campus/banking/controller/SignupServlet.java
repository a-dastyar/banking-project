package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.service.UserService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/signup")
public class SignupServlet  extends HttpServlet {

    private UserService service;

    @Inject
    public void initialize(UserService service) {
        this.service = service;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        if (req.getUserPrincipal() != null){
            resp.sendRedirect(req.getContextPath());
        }else{
            req.getRequestDispatcher("/views/pages/singles/signup.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        this.service.signup(UserService.toUser(req.getParameterMap()));
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}