package com.example.runeforgemarket.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.runeforgemarket.wallet.model.WalletBalance;
import com.example.runeforgemarket.wallet.model.WalletBalance.WalletBalanceId;

public interface WalletBalanceRepository extends JpaRepository<WalletBalance, WalletBalanceId> {
}
