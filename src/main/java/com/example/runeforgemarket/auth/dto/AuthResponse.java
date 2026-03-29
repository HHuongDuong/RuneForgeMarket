package com.example.runeforgemarket.auth.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType
) {
}
