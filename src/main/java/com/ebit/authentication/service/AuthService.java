package com.ebit.authentication.service;

import com.ebit.authentication.entity.User;
import com.ebit.authentication.payloads.AuthRequestDto;

public interface AuthService {
    User saveOrUpdateUser(Object userDto);
    String authenticate(AuthRequestDto authRequestDto);
    boolean changePassword(String email, String oldPassword, String newPassword);
}
