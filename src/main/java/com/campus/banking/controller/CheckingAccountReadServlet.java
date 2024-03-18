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
@WebServlet("/checking_account")
public class CheckingAccountReadServlet extends HttpServlet {

    private CheckingAccountService service;

    @Inject
    public void initialize(CheckingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var session = req.getSession(false);
        if (session != null) {
            var accountNumber = (String) session.getAttribute("checking_account_number");
            if (accountNumber != null) {
                var account = service.getByAccountNumber(accountNumber);
                req.setAttribute("checking_account", account);
            }
        }
        req.getRequestDispatcher("/views/pages/checking_account.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var accountNumber = req.getParameter("checking_account_number_r");

        var session = req.getSession(false);
        if (session != null) {
            session.setAttribute("checking_account_number", accountNumber);
        }
        resp.sendRedirect(req.getContextPath() + "/checking_account");
    }
}
