package com.ebit.authentication.controller;

import com.ebit.authentication.entity.User;
import com.ebit.authentication.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class ProfileController {



}
