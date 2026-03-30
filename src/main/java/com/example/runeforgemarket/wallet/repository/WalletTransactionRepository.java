package com.example.runeforgemarket.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.runeforgemarket.wallet.model.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWallet_IdAndCurrency_IdOrderByCreatedAtDesc(Long walletId, Integer currencyId);
}
