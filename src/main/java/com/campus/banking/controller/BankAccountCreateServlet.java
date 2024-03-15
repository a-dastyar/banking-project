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
@WebServlet("/bank_account_create")
public class BankAccountCreateServlet extends HttpServlet {

    private BankAccountService<BankAccount> service;

    @Inject
    public void initialize(BankAccountService<BankAccount> service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        BankAccount account = BankAccountService.toBankAccount(req.getParameterMap());
        this.service.add(account);
        req.setAttribute("account", account);
        req.getRequestDispatcher("/views/bank_account.jsp").forward(req, resp);
    }
}
