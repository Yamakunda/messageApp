package com.messager.messager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messager.messager.dto.AccountDTO.response.AccountResponse;
import com.messager.messager.service.AccountService;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/all")
    public ResponseEntity<List<AccountResponse>> getAllUsers() {
        List<AccountResponse> accounts = accountService.getAllAccountList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        AccountResponse currentUser = accountService.getCurrentAccount();
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser); 
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
}
