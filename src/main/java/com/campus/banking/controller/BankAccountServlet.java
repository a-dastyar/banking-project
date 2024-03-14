package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

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
public class BankAccountServlet extends HttpServlet {

    private BankAccountService<BankAccount> service;

    @Inject
    public void initialize(BankAccountService<BankAccount> service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        req.getRequestDispatcher("/views/bank_account.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var accountNumber = Optional.ofNullable(req.getParameter("accountNumber"));
        if (accountNumber.isPresent()) {
            var account = service.getByAccountNumber(accountNumber.get());
            req.setAttribute("account", account);
        } else {
            req.setAttribute("account", null);
        }
        req.getRequestDispatcher("/views/bank_account.jsp").forward(req, resp);
    }
}
