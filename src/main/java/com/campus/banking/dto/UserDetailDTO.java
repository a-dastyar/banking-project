package com.campus.banking.dto;

import java.util.List;
import java.util.Map;

import com.campus.banking.model.BankAccount;
import com.campus.banking.model.CheckingAccount;
import com.campus.banking.model.Role;
import com.campus.banking.model.SavingAccount;
import com.campus.banking.model.User;
import com.campus.banking.persistence.Page;

import lombok.Builder;

@Builder
public record UserDetailDTO(User user,
        Page<BankAccount> bankAccounts,
        Page<CheckingAccount> checkingAccounts,
        Page<SavingAccount> savingAccounts,
        Map<String, Boolean> userRoles,
        List<Role> availableRoles,
        String activeTab) {

}
