package com.ebit.authentication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String status(){
        return "Running..";
    }

    @GetMapping("/private")
    public String privateController(){
        return "Access only authenticated users";
    }

    @GetMapping("/a")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public String accessAdmin(){
        return "Admin access granted";
    }

    @GetMapping("/u")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String userAdmin(){
        return "User access granted";
    }
}
