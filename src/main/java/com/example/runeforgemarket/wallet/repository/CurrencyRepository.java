package com.example.runeforgemarket.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.runeforgemarket.common.currency.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
