package com.example.runeforgemarket.common.currency.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.runeforgemarket.common.currency.model.Currency;
import com.example.runeforgemarket.common.currency.repository.CurrencyRepository;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public Currency getCurrency(Integer currencyId) {
        return currencyRepository.findById(currencyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found"));
    }
    
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
}
