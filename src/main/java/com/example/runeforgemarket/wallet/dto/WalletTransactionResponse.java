package com.example.runeforgemarket.wallet.dto;

import java.time.Instant;
import com.example.runeforgemarket.wallet.model.TransactionRefType;
import com.example.runeforgemarket.wallet.model.TransactionType;

public record WalletTransactionResponse(
    Long id,
    Long walletId,
    Integer currencyId,
    Long amount,
    Long balanceAfter,
    TransactionType type,
    TransactionRefType refType,
    Instant createdAt
) {
}
