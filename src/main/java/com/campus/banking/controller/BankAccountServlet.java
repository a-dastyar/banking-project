package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Role;
import com.campus.banking.service.BankAccountService;
import com.campus.banking.util.ServletUtils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpMethodConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebServlet("/bank-accounts")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class BankAccountServlet extends HttpServlet {

    private BankAccountService<BankAccount> service;

    @Inject
    public void initialize(BankAccountService<BankAccount> service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var page = ServletUtils.getPageNumber(req.getParameter("page"));
        var result = service.getPage(page);
        req.setAttribute("accounts", result);

        var min = Optional.ofNullable(req.getParameter("sum_min"));
        if (min.isPresent()) {
            var val = ServletUtils.getDoubleValue(min.get());
            var sum = service.sumBalanceHigherThan(val);
            req.setAttribute("min", val);
            req.setAttribute("sum", sum);
        }
        req.getRequestDispatcher("/views/pages/accounts/bank_accounts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");

        var account = BankAccountService.toBankAccount(req.getParameterMap());
        this.service.add(account);

        resp.sendRedirect(req.getContextPath() + "/bank-accounts/details?account_number=" + account.getAccountNumber());
    }
}
