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
@WebServlet("/saving-accounts/details")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class SavingAccountDetailServlet extends HttpServlet {

    private SavingAccountService service;

    @Inject
    public void initialize(SavingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var accountNumber = Optional.ofNullable(req.getParameter("account_number"))
                .orElseThrow(IllegalArgumentException::new);
        var trxPage = ServletUtils.getPageNumber(req.getParameter("transaction_page"));

        var account = service.getByAccountNumber(accountNumber);
        var transactions = service.getTransactions(accountNumber, trxPage);
        
        req.setAttribute("account", account);
        req.setAttribute("transactions", transactions);
        req.getRequestDispatcher("/views/pages/saving_account_detail.jsp").forward(req, resp);
    }
}
