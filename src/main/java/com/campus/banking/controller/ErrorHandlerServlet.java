package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.exception.DuplicatedException;
import com.campus.banking.exception.NotFoundException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/errors")
public class ErrorHandlerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Handling error");
        var exception = req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        var error = switch (exception) {
            case NotFoundException e -> "404.jsp";
            case DuplicatedException e -> "400.jsp";
            case ConstraintViolationException e -> "400.jsp";
            default -> "500.jsp";
        };
        log.debug("forwarding to {} for exception {}", error, exception);
        req.getRequestDispatcher("/views/errors/" + error).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}