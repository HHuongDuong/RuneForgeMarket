package com.example.runeforgemarket.common.currency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.runeforgemarket.common.currency.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
}
