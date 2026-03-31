package com.example.runeforgemarket.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.wallet.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("""
        select w
        from Wallet w
        where w.userId = :userId
        """)
    Optional<Wallet> findWalletByUserId(@Param("userId") Long userId);
}
