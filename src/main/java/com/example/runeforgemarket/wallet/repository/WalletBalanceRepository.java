package com.example.runeforgemarket.wallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.wallet.model.WalletBalance;
import com.example.runeforgemarket.wallet.model.WalletBalance.WalletBalanceId;

public interface WalletBalanceRepository extends JpaRepository<WalletBalance, WalletBalanceId> {
	@Query("""
		select wb
		from WalletBalance wb
		where wb.wallet.id = :walletId
		""")
	List<WalletBalance> findByWalletId(@Param("walletId") Long walletId);
}
