package com.example.runeforgemarket.wallet.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.runeforgemarket.wallet.dto.CreateWalletTransactionRequest;
import com.example.runeforgemarket.wallet.dto.WalletBalanceResponse;
import com.example.runeforgemarket.wallet.dto.WalletTransactionResponse;
import com.example.runeforgemarket.wallet.service.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public WalletBalanceResponse getBalance(@RequestParam Integer currencyId) {
        return walletService.getBalance(currencyId);
    }

    @GetMapping("/balances")
    public List<WalletBalanceResponse> getBalances() {
        return walletService.getBalances();
    }

    @GetMapping("/transactions")
    public List<WalletTransactionResponse> getTransactions(
        @RequestParam(required = false) Integer currencyId
    ) {
        return walletService.getTransactions(currencyId);
    }

    // For testing purposes, in production this should be protected and only allow certain ref types
    @PostMapping("/transactions")
    public WalletTransactionResponse applyTransaction(
        @Valid @RequestBody CreateWalletTransactionRequest request
    ) {
        return walletService.applyTransaction(request);
    }
}
