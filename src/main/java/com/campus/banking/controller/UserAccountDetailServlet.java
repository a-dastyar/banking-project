package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;

import com.campus.banking.dto.AccountDetailDTO;
import com.campus.banking.exception.InvalidArgumentException;
import com.campus.banking.exception.RequiredParamException;
import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Role;
import com.campus.banking.service.BankAccountService;
import com.campus.banking.service.CheckingAccountService;
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
@WebServlet("/dashboard/account-details")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MEMBER, Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class UserAccountDetailServlet extends HttpServlet {

    private BankAccountService<BankAccount> account;
    private CheckingAccountService checking;
    private SavingAccountService saving;

    @Inject
    public void initialize(BankAccountService<BankAccount> account,
            CheckingAccountService checking,
            SavingAccountService saving) {
        this.account = account;
        this.checking = checking;
        this.saving = saving;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var accountNumber = Optional.ofNullable(req.getParameter("account_number"))
                .orElseThrow(() -> RequiredParamException.getException("account_number"));
        var accountType = ServletUtils.getAccountType(req.getParameter("account_type"))
                .orElseThrow(() -> RequiredParamException.getException("account_type"));
        var trxPage = ServletUtils.getPositiveIntWithDefault(req.getParameter("transaction_page"), "1")
                .orElseThrow(() -> InvalidArgumentException.NON_POSITIVE_INTEGER);
        var size = ServletUtils.getPositiveInt(req.getParameter("size"));

        var service = switch (accountType) {
            case BANK -> account;
            case CHECKING -> checking;
            case SAVING -> saving;
        };

        var account = service.getByAccountNumber(accountNumber);
        var transactions = service.getTransactions(accountNumber, trxPage, size);

        var accountDetails = AccountDetailDTO.builder()
                .account(account)
                .transactions(transactions)
                .maxWithdraw(0)
                .minDeposit(0)
                .build();

        req.setAttribute("accountType", accountType.toString());
        req.setAttribute("accountDetails", accountDetails);
        req.getRequestDispatcher("/views/pages/users/user_account_details.jsp").forward(req, resp);
    }
}
