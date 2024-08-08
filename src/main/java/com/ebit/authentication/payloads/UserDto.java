package com.ebit.authentication.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotNull(message = "first name is mandatory")
    private String firstName;
    private String lastName;
    @NotNull(message = "phone number is mandatory")
    @Size(min = 10, max = 10, message = "invalid phone number")
    @Positive(message = "invalid phone number")
    private String phone;
    @NotNull(message = "email is mandatory")
    @Email
    private String email;
    @NotNull(message = "password is mandatory")
    @Size(min = 5, message = "password must be at least 5 character")
    private String password;
}
