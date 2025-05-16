package com.messager.messager.dto.AccountDTO.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
  private String username;
  private String email;
  private Long accountId;
  private Set<String> roles;
}
