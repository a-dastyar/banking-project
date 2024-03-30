package com.campus.banking.controller;

import static com.campus.banking.util.ServletUtils.getDoubleValue;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.model.Role;
import com.campus.banking.service.CheckingAccountService;
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
@WebServlet("/checking-accounts")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class CheckingAccountServlet extends HttpServlet {

    private CheckingAccountService service;

    @Inject
    public void initialize(CheckingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var page = ServletUtils.getPositiveIntWithDefault(req.getParameter("page"),"1")
                .orElseThrow(() -> InvalidArgumentException.NON_POSITIVE_INTEGER);
        var size = ServletUtils.getPositiveInt(req.getParameter("size"));

        var result = service.getPage(page, size);
        var minInitialBalance = service.getMinimumInitialBalance();
        
        var min = Optional.ofNullable(req.getParameter("sum_min"));
        if (min.isPresent()) {
            var val = getDoubleValue(min.get());
            var sum = service.sumBalanceHigherThan(val);
            req.setAttribute("min", val);
            req.setAttribute("sum", sum);
        }

        req.setAttribute("accounts", result);
        req.setAttribute("minInitialBalance", minInitialBalance);
        req.getRequestDispatcher("/views/pages/accounts/checking_accounts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");

        var account = CheckingAccountService.toCheckingAccount(req.getParameterMap());
        this.service.add(account);

        resp.sendRedirect(
                req.getContextPath() + "/checking-accounts/details?account_number=" + account.getAccountNumber());
    }
}
