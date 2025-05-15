package com.messager.messager.dto.AccountDTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank
  private String username;
  
  @NotBlank
  private String password;
  
}
