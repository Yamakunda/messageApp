package com.messager.messager.security;

import com.messager.messager.model.Account; // Your Account entity
import com.messager.messager.repository.AccountRepository; // Your AccountRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Make this a Spring Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository; // Use your AccountRepository

    @Override
    @Transactional // Good practice
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch your custom Account entity
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // 2. Convert roles from Set<String> to Collection<? extends GrantedAuthority>
        // Assuming your Account entity has getRoles() returning Set<String>
        List<GrantedAuthority> authorities = account.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 3. Create and return Spring Security's User object
        // This User object contains the *hashed* password for Spring Security to verify.
        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(), // Pass the HASHED password from your Account entity
                authorities

        );
    }
}