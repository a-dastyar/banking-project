package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.service.CheckingAccountService;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/checking_account_withdraw")
public class CheckingAccountWithdrawServlet extends HttpServlet {

    private CheckingAccountService service;

    @Inject
    public void initialize(CheckingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var session = req.getSession(false);
        if (session != null) {
            var accountNumber = (String) session.getAttribute("account_number");
            var amount = Optional.ofNullable(req.getParameter("withdraw_amount"))
                    .filter(str -> str.chars().allMatch(this::isNum))
                    .map(Double::valueOf)
                    .filter(i -> i >= 0.0d)
                    .orElse(0.0d);
            this.service.withdraw(accountNumber, amount);
        }

        resp.sendRedirect(req.getContextPath() + "/checking_account");
    }

    private boolean isNum(int codePonit) {
        return Character.isDigit(codePonit) || codePonit == ".".codePointAt(0);
    }
}
