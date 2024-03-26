package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.dto.AccountDetailDTO;
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
@WebServlet("/bank-accounts/details")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class BankAccountDetailServlet extends HttpServlet {

    private BankAccountService<BankAccount> service;

    @Inject
    public void initialize(BankAccountService<BankAccount> service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var accountNumber = Optional.ofNullable(req.getParameter("account_number"))
                .orElseThrow(IllegalArgumentException::new);
        var trxPage = ServletUtils.getPositiveIntWithDefault(req.getParameter("transaction_page"),"1")
                .orElseThrow(IllegalArgumentException::new);

        var account = service.getByAccountNumber(accountNumber);
        var transactions = service.getTransactions(accountNumber, trxPage);
        var maxWithdraw = service.getAllowedWithdraw(account);
        var minDeposit = service.getAllowedWithdraw(account);

        var accountDetails = AccountDetailDTO.builder()
                .account(account)
                .maxWithdraw(maxWithdraw)
                .minDeposit(minDeposit)
                .transactions(transactions)
                .build();

        req.setAttribute("accountDetails", accountDetails);
        req.getRequestDispatcher("/views/pages/accounts/bank_account_details.jsp").forward(req, resp);
    }
}
