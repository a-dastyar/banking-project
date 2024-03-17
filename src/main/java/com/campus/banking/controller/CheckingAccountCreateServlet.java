package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.service.CheckingAccountService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/checking_account_create")
public class CheckingAccountCreateServlet extends HttpServlet {

    private CheckingAccountService service;

    @Inject
    public void initialize(CheckingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");

        var account = CheckingAccountService.toCheckingAccount(req.getParameterMap());
        this.service.add(account);

        var session = req.getSession();
        session.setAttribute("checking_account_number", account.getAccountNumber());

        resp.sendRedirect(req.getContextPath() + "/checking_account");
    }
}
