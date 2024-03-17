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
@WebServlet("/saving_account")
public class SavingAccountReadServlet extends HttpServlet {

    private SavingAccountService service;

    @Inject
    public void initialize(SavingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var session = req.getSession(false);
        if (session != null) {
            var accountNumber = (String) session.getAttribute("saving_account_number");
            if (accountNumber != null) {
                var account = service.getByAccountNumber(accountNumber);
                req.setAttribute("saving_account", account);
            }
        }
        req.getRequestDispatcher("/views/saving_account.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var accountNumber = req.getParameter("cheking_account_number_r");

        var session = req.getSession(false);
        if (session != null) {
            session.setAttribute("saving_account_number", accountNumber);
        }
        resp.sendRedirect(req.getContextPath() + "/saving_account");
    }
}
