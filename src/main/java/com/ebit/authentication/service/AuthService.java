package com.ebit.authentication.service;

import com.ebit.authentication.entity.User;
import com.ebit.authentication.payloads.UserDto;

public interface AuthService {
    User createUser(UserDto userDto);



    User updateUser(Long id, UserDto userDto);
}
