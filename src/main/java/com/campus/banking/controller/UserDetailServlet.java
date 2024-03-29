package com.campus.banking.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.Role;
import com.campus.banking.service.BankAccountService;
import com.campus.banking.service.CheckingAccountService;
import com.campus.banking.service.SavingAccountService;
import com.campus.banking.service.UserService;

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
@WebServlet("/users/details")
@ServletSecurity(httpMethodConstraints = {
        @HttpMethodConstraint(value = "GET", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN }),
        @HttpMethodConstraint(value = "POST", rolesAllowed = { Role.CONST.MANAGER, Role.CONST.ADMIN })
})
public class UserDetailServlet extends HttpServlet {

    private UserService service;
    private BankAccountService<BankAccount> account;
    private CheckingAccountService checking;
    private SavingAccountService saving;

    @Inject
    public void initialize(UserService service, BankAccountService<BankAccount> account,
            CheckingAccountService checking,
            SavingAccountService saving) {
        this.service = service;
        this.account = account;
        this.checking = checking;
        this.saving = saving;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET");
        var username = Optional.ofNullable(req.getParameter("username"))
                .orElseThrow(IllegalArgumentException::new);

        var user = service.getByUsername(username);
        var bankAccounts = account.getByUsername(username);
        var checkingAccounts = checking.getByUsername(username);
        var savingAccounts = saving.getByUsername(username);
        
        req.setAttribute("user", user);
        req.setAttribute("bankAccounts", bankAccounts);
        req.setAttribute("checkingAccounts", checkingAccounts);
        req.setAttribute("savingAccounts", savingAccounts);

        var userRoles = user.getRoles().stream().collect(Collectors.toMap(Role::toString, r -> true));

        req.setAttribute("roles", Role.values());
        req.setAttribute("userRoles", userRoles);

        req.getRequestDispatcher("/views/pages/users/user_details.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("POST");
        var user = UserService.toUser(req.getParameterMap());
        this.service.update(user);
        resp.sendRedirect(req.getContextPath() + "/users/details?username=" + user.getUsername());
    }
}