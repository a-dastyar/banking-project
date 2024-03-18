package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.model.Role;
import com.campus.banking.service.SavingAccountService;
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
@WebServlet("/saving-accounts")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class SavingAccountServlet extends HttpServlet {

    private SavingAccountService service;

    @Inject
    public void initialize(SavingAccountService service) {
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
        req.getRequestDispatcher("/views/pages/saving_accounts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");

        var account = SavingAccountService.toSavingAccount(req.getParameterMap());
        this.service.add(account);

        resp.sendRedirect(req.getContextPath() + "/saving-accounts/details?account_number=" + account.getAccountNumber());
    }
}
