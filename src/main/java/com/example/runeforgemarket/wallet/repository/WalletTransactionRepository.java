package com.example.runeforgemarket.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.wallet.model.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    @Query("""
        select tx
        from WalletTransaction tx
        where tx.wallet.id = :walletId
        order by tx.createdAt desc
        """)
    List<WalletTransaction> findByWallet(@Param("walletId") Long walletId);

    @Query("""
        select tx
        from WalletTransaction tx
        where tx.wallet.id = :walletId
          and tx.currency.id = :currencyId
        order by tx.createdAt desc
        """)
    List<WalletTransaction> findByWalletAndCurrency(
        @Param("walletId") Long walletId,
        @Param("currencyId") Integer currencyId
    );
}
