package com.campus.banking.dto;

import java.util.List;
import java.util.Map;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.Role;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;

import lombok.Builder;

@Builder
public record UserDetailDTO(User user,
        List<BankAccount> bankAccounts,
        List<CheckingAccount> checkingAccounts,
        List<SavingAccount> savingAccounts,
        Map<String,Boolean> userRoles,
        List<Role> availableRoles) {

}
