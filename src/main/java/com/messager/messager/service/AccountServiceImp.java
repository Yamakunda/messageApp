package com.messager.messager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.messager.messager.dto.response.AccountResponse;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        Account acc = (Account) authentication.getPrincipal();
        String username = acc.getUsername();

        Account current = accountRepository.findByUsername(username)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Authenticated user not found in database: " + username));
        logger.info("Current user: {}", current);
        return new AccountResponse(current.getUsername(), current.getEmail(), current.getRoles());
    }
    @Override
    public Account loadAccountByUsername(String username)  {
        Account acc = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Not Found with username: " + username));
        return acc;
    }

}
