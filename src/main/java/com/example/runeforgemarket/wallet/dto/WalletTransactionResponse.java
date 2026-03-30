package com.example.runeforgemarket.wallet.dto;

import java.time.Instant;

public record WalletTransactionResponse(
    Long id,
    Long walletId,
    Integer currencyId,
    Long amount,
    Long balanceAfter,
    String type,
    String refType,
    Instant createdAt
) {
}
