package com.messager.messager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;

    public AuthResponse(String accessToken, String username) {
        this.accessToken = accessToken;
        this.username = username;
    }
}
