package com.messager.messager.service;

import java.util.List;

import com.messager.messager.dto.AccountDTO.response.AccountResponse;
import com.messager.messager.model.Account;

public interface AccountService {
    public Account loadAccountByUsername(String username);
    public AccountResponse getCurrentAccount();
    public List<AccountResponse> getAllAccountList();
} 
