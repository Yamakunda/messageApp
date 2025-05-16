package com.messager.messager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.messager.messager.dto.AccountDTO.response.AccountResponse;
import com.messager.messager.exception.ResourceNotFoundException;
import com.messager.messager.model.Account;
import com.messager.messager.repository.AccountRepository;

@Service
public class AccountServiceImp implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImp.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountResponse getCurrentAccount() {
        logger.info("Fetching current account");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication: {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        logger.info("Current Principal: {}",authentication.getPrincipal());
        Account acc = (Account) authentication.getPrincipal();
        String username = acc.getUsername();

        Account current = accountRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Authenticated user not found in database: " + username));
        logger.info("Current user: {}", current);
        return new AccountResponse(current.getUsername(), current.getEmail(), current.getId(), current.getRoles());
    }
    @Override
    public Account loadAccountByUsername(String username)  {
        Account acc = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Not Found with username: " + username));
        return acc;
    }

    @Override
    public List<AccountResponse> getAllAccountList() {
        List<AccountResponse> accounts = accountRepository.findAll().stream()
            .map(account -> {
                AccountResponse dto = new AccountResponse();
                dto.setAccountId(account.getId());
                dto.setEmail(account.getEmail());
                dto.setUsername(account.getUsername());
                dto.setRoles(account.getRoles());
                return dto;
            })
            .collect(Collectors.toList());
        return accounts;
    }

}
