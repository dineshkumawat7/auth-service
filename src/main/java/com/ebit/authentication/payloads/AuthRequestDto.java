package com.ebit.authentication.payloads;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequestDto {
    @NotNull(message = "username or email is mandatory")
    private String username;
    @NotNull(message = "password is mandatory")
    private String password;
}
