package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.model.BankAccount;
import com.campus.banking.service.BankAccountService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/bank_account")
public class BankAccountReadServlet extends HttpServlet {

    private BankAccountService<BankAccount> service;

    @Inject
    public void initialize(BankAccountService<BankAccount> service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        // req.getSession() is only called in BankAccountCreateServlet
        // so if session is not null, then account_number is not null
        var session = req.getSession(false);
        if (session != null) {
            var accountNumber = (String) session.getAttribute("account_number");
            var account = service.getByAccountNumber(accountNumber);
            req.setAttribute("account", account);
        }
        req.getRequestDispatcher("/views/bank_account.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var accountNumber = req.getParameter("account_number");

        var session = req.getSession(false);
        if (session != null) {
            session.setAttribute("account_number", accountNumber);
        }
        resp.sendRedirect(req.getContextPath() + "/bank_account");
    }
}
