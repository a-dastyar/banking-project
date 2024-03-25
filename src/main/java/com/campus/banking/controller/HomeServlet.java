package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.service.HashService;

import jakarta.inject.Inject;
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
    HashService hash;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        req.getRequestDispatcher("/views/pages/singles/home.jsp").forward(req, resp);
    }
}
