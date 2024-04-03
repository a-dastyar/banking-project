package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.exception.RequiredParamException;
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
@WebServlet("/saving-accounts/balance")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class SavingAccountBalanceServlet extends HttpServlet {

    private SavingAccountService service;

    @Inject
    public void initialize(SavingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var account_number = Optional.ofNullable(req.getParameter("account_number"))
                .orElseThrow(() -> RequiredParamException.getException("account_number"));
        var amount = ServletUtils.getDoubleValue(req.getParameter("amount"));
        var type = ServletUtils.getTransactionType(req.getParameter("type"));

        switch (type) {
            case WITHDRAW -> service.withdraw(account_number, amount);
            case DEPOSIT -> service.deposit(account_number, amount);
        }
        
        resp.sendRedirect(req.getContextPath() + "/saving-accounts/details?account_number=" + account_number);
    }
}
