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
@WebServlet("/bank_account_sum_balance")
public class BankAccountSumBalanceServlet extends HttpServlet {

    private BankAccountService<BankAccount> service;

    @Inject
    public void initialize(BankAccountService<BankAccount> service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var limit = Optional.ofNullable(req.getParameter("limit"))
                .filter(str -> str.chars().allMatch(this::isNum))
                .map(Double::valueOf)
                .filter(i -> i >= 0.0d)
                .orElse(0.0d);
        double sum = this.service.sumBalanceHigherThan(limit);
        req.setAttribute("sum", sum);
        req.setAttribute("limit", limit);
        req.getRequestDispatcher("/views/pages/bank_account.jsp").forward(req, resp);
    }

    private boolean isNum(int codePonit) {
        return Character.isDigit(codePonit) || codePonit == ".".codePointAt(0);
    }
}
