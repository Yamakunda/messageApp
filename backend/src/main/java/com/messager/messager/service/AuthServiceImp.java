package com.messager.messager.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.messager.messager.dto.AccountDTO.request.LoginRequest;
import com.messager.messager.dto.AccountDTO.request.RegisterRequest;
import com.messager.messager.dto.AccountDTO.response.AuthResponse;
import com.messager.messager.exception.ResourceExistedException;
import com.messager.messager.exception.ResourceNotFoundException;
import com.messager.messager.model.Account;
import com.messager.messager.repository.AccountRepository;
import com.messager.messager.security.JwtTokenProvider;

@Service
public class AuthServiceImp implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImp.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public Account registerAccount(RegisterRequest registerRequest) {
        if (accountRepository.existsByUsername(registerRequest.getUsername())) {
            throw new ResourceExistedException("Username is already taken!");
        }

        if (accountRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResourceExistedException("Email Address already in use!");
        }

        Account user = new Account();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(Set.of("ROLE_USER")); // Default role

        return accountRepository.save(user);
    }

    @Override
    public AuthResponse authenticateAccount(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Security context updated with authentication for user: {}", authentication);

        User user = (User) authentication.getPrincipal();
        // Account acc = (Account) authentication.getPrincipal();
        String username = user.getUsername();

        // Fetch your custom User entity to get its ID and other details
        Account customUser = accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found after authentication: " + username));

        String jwt = tokenProvider.generateToken(username, customUser.getId());

        return new AuthResponse(jwt,"Bearer", customUser.getUsername(), customUser.getId());
    }
}
