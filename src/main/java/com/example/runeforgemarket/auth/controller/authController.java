package com.example.runeforgemarket.auth.controller;

import com.example.runeforgemarket.auth.dto.AuthResponse;
import com.example.runeforgemarket.auth.dto.LoginRequest;
import com.example.runeforgemarket.auth.dto.RefreshRequest;
import com.example.runeforgemarket.auth.dto.RegisterRequest;
import com.example.runeforgemarket.auth.service.authService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class authController {

    private final authService authService;

    public authController(authService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }
}
