package com.ebit.authentication.payloads;

import lombok.Data;

@Data
public class LoginResponse {
    private String tokenType = "Bearer ";
    private String token;
}
