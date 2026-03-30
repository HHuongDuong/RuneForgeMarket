package com.example.runeforgemarket.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWalletTransactionRequest(
    @NotNull Integer currencyId,
    @NotNull Long amount,
    @NotBlank String type,
    @NotBlank String refType
) {
}
