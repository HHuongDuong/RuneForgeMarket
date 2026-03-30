package com.example.runeforgemarket.wallet.dto;

public record WalletBalanceResponse(
    Long walletId,
    Integer currencyId,
    Long balance
) {
}
