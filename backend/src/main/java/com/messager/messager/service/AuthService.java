package com.messager.messager.service;

import com.messager.messager.dto.AccountDTO.request.LoginRequest;
import com.messager.messager.dto.AccountDTO.request.RegisterRequest;
import com.messager.messager.dto.AccountDTO.response.AuthResponse;
import com.messager.messager.model.Account;

public interface AuthService {
      public Account registerAccount(RegisterRequest registerRequest);
    public AuthResponse authenticateAccount(LoginRequest loginRequest);

} 
