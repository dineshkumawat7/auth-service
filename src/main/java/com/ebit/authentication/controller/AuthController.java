package com.ebit.authentication.controller;

import com.ebit.authentication.config.JwtUtils;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.payloads.AuthRequestDto;
import com.ebit.authentication.payloads.LoginResponse;
import com.ebit.authentication.payloads.UserRegistrationDto;
import com.ebit.authentication.payloads.UserUpdateDto;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.service.AuthService;
import com.ebit.authentication.utils.ApiResponse;
import com.ebit.authentication.utils.ErrorResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> saveOrUpdateUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        User user = authService.saveOrUpdateUser(userRegistrationDto);
        user.setPassword("*****");
        String message = "new user successfully registered";
        ApiResponse<User> response = ApiResponse.<User>builder().timestamp(LocalDateTime.now()).status("success").message(message).data(user).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequestDto authRequestDto) throws Exception {
        LoginResponse response = new LoginResponse();
        String token = authService.authenticate(authRequestDto);
        response.setToken(token);
        return ResponseEntity.status(HttpStatus.OK).header("Authorization", token).body(response);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object user = authentication.getPrincipal();
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("user not logged in");
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        User user = authService.saveOrUpdateUser(userUpdateDto);
        user.setPassword("*****");
        String message = "user account updated successfully";
        ApiResponse<User> response = ApiResponse.<User>builder().timestamp(LocalDateTime.now()).status("success").message(message).data(user).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
