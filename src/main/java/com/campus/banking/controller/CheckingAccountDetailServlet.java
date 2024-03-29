package com.campus.banking.controller;

import static com.campus.banking.util.ServletUtils.getPositiveIntWithDefault;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.dto.AccountDetailDTO;
import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.exception.RequiredParamException;
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
@WebServlet("/checking-accounts/details")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class CheckingAccountDetailServlet extends HttpServlet {

    private CheckingAccountService service;

    @Inject
    public void initialize(CheckingAccountService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var accountNumber = Optional.ofNullable(req.getParameter("account_number"))
                .orElseThrow(() -> RequiredParamException.getException("account_number"));
        var trxPage = getPositiveIntWithDefault(req.getParameter("transaction_page"),"1")
                .orElseThrow(() -> InvalidArgumentException.NON_POSITIVE_INTEGER);
        var size = ServletUtils.getPositiveInt(req.getParameter("size"));

        var account = service.getByAccountNumber(accountNumber);
        var transactions = service.getTransactions(accountNumber, trxPage, size);
        var maxWithdraw = service.getAllowedWithdraw(account);
        var minWithdraw = service.getMinimumWithdraw(account);
        var minDeposit = service.getMinimumDeposit(account);

        var accountDetails = AccountDetailDTO.builder()
                .account(account)
                .maxWithdraw(maxWithdraw)
                .minWithdraw(minWithdraw)
                .minDeposit(minDeposit)
                .transactions(transactions)
                .build();
                
        req.setAttribute("accountDetails", accountDetails);
        req.getRequestDispatcher("/views/pages/accounts/checking_account_details.jsp").forward(req, resp);
    }
}
