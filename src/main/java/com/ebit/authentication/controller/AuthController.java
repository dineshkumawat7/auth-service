package com.ebit.authentication.controller;

import com.ebit.authentication.config.JwtUtils;
import com.ebit.authentication.entity.User;
import com.ebit.authentication.exception.UserAlreadyExistsException;
import com.ebit.authentication.payloads.LoginResponse;
import com.ebit.authentication.payloads.AuthRequestDto;
import com.ebit.authentication.payloads.UserDto;
import com.ebit.authentication.repository.AuthRepository;
import com.ebit.authentication.service.AuthService;
import com.ebit.authentication.utils.ApiResponse;
import com.ebit.authentication.utils.ErrorResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> createNewUser(@Valid @RequestBody UserDto userDto) {
        ApiResponse<User> response = new ApiResponse<>();
        try {
            if (authRepository.existsByEmail(userDto.getEmail())) {
                throw new UserAlreadyExistsException("user already exists with email " + userDto.getEmail());
            }
            User user = authService.createUser(userDto);
            user.setPassword("*****");
            response.setStatusCode(HttpStatus.CREATED.value());
            response.setStatus("success");
            response.setMessage("New user successfully registered");
            response.setData(user);
            logger.info("new user registration successfully with email '{}'", userDto.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setStatus("error");
            response.setMessage(e.getMessage());
            response.setData(null);
            logger.error("registration failed with email '{}'", userDto.getEmail());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody AuthRequestDto authRequestDto) throws Exception {
        LoginResponse response = new LoginResponse();
        Authentication authentication = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtUtils.generateToken(authRequestDto.getUsername());
            logger.info("Authentication success with username '{}'", authRequestDto.getUsername());
            response.setToken(token);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            Object user =  authentication.getPrincipal();
            return new ResponseEntity<>(user ,HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not logged in");
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> updateUser(@RequestParam Long id, @Valid @RequestBody UserDto userDto){
        ApiResponse<User> response = new ApiResponse<>();
        try{
            User user = authService.updateUser(id, userDto);
            response.setStatusCode(HttpStatus.OK.value());
            response.setStatus("success");
            response.setMessage("User details updated successfully");
            response.setData(user);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.setStatus("error");
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
