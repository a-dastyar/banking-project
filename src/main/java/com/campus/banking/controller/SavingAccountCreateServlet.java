package com.campus.banking.controller;

import java.io.IOException;

import com.campus.banking.service.SavingAccountService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/saving_account_create")
public class SavingAccountCreateServlet extends HttpServlet {

    private SavingAccountService service;

    @Inject
    public void initialize(SavingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");

        var account = SavingAccountService.toSavingAccount(req.getParameterMap());
        this.service.add(account);

        var session = req.getSession();
        session.setAttribute("saving_account_number", account.getAccountNumber());

        resp.sendRedirect(req.getContextPath() + "/saving_account");
    }
}
