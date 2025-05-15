package com.messager.messager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messager.messager.dto.request.LoginRequest;
import com.messager.messager.dto.request.RegisterRequest;
import com.messager.messager.dto.response.AuthResponse;
import com.messager.messager.model.Account;
import com.messager.messager.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Account registeredAccount = authService.registerAccount(registerRequest);
            // You might want to return a simpler DTO than the full User entity
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body("User registered successfully! Username: " + registeredAccount.getUsername());
        } catch (Exception e) { // Catch specific exceptions for better error handling
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticateAccount(loginRequest);
        return ResponseEntity.ok(authResponse);
    }


}
