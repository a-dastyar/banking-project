package com.campus.banking.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.campus.banking.exception.DuplicatedException;
import com.campus.banking.exception.IllegalBalanceStateException;
import com.campus.banking.exception.InsufficientFundsException;
import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.exception.InvalidTransactionException;
import com.campus.banking.exception.LessThanMinimumTransactionException;
import com.campus.banking.exception.NotFoundException;
import com.campus.banking.exception.RequiredParamException;

import jakarta.inject.Inject;
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

    private boolean debug;

    @Inject
    public void initialize(@ConfigProperty(name = "app.mode.debug") boolean debug) {
        this.debug = debug;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Handling error");
        var exception = req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        var errorPage = switch (exception) {
            case DuplicatedException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case IllegalBalanceStateException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case InsufficientFundsException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case InvalidArgumentException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case InvalidTransactionException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case LessThanMinimumTransactionException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case NotFoundException e -> {
                resp.setStatus(404);
                yield "404.jsp";
            }
            case RequiredParamException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case IllegalArgumentException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            case ConstraintViolationException e -> {
                resp.setStatus(400);
                yield "400.jsp";
            }
            default -> "500.jsp";
        };
        if (debug) {
            var error = switch (exception) {
                case Exception e -> getError(e);
                default -> null;
            };
            req.setAttribute("error", error);
        }
        log.debug("forwarding to {} for exception {}", errorPage, exception);
        req.getRequestDispatcher("/views/errors/" + errorPage).forward(req, resp);
    }

    private String getError(Exception e) {
        var stackTrace = Arrays.stream(e.getStackTrace())
                .map(Object::toString)
                .map(line->line.replace("<", "&lt"))
                .map(line->line.replace(">", "&gt"))
                .collect(Collectors.joining("\n"));
        log.debug(stackTrace);
        var message = "%s\n%s".formatted(e.getMessage(), stackTrace);
        return message;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
