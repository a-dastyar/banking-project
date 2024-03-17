package com.campus.banking.controller;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("")
public class HomeServlet extends HttpServlet {

    @Inject
    public void initialize(EntityManager em) {
        log.debug("Setting up database");
        em.isOpen();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/pages/home.jsp").forward(req, resp);
    }
}
