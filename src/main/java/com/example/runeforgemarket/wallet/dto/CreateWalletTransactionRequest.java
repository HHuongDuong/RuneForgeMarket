package com.example.runeforgemarket.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.runeforgemarket.wallet.model.TransactionRefType;
import com.example.runeforgemarket.wallet.model.TransactionType;


public record CreateWalletTransactionRequest(
    @NotNull Integer currencyId,
    @NotNull Long amount,
    @NotBlank TransactionType type,
    @NotBlank TransactionRefType refType
) {
}
