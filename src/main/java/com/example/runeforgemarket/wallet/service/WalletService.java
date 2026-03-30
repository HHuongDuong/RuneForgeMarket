package com.example.runeforgemarket.wallet.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.runeforgemarket.common.currency.Currency;
import com.example.runeforgemarket.user.model.User;
import com.example.runeforgemarket.user.repository.UserRepository;
import com.example.runeforgemarket.wallet.dto.CreateWalletTransactionRequest;
import com.example.runeforgemarket.wallet.dto.WalletBalanceResponse;
import com.example.runeforgemarket.wallet.dto.WalletTransactionResponse;
import com.example.runeforgemarket.wallet.model.Wallet;
import com.example.runeforgemarket.wallet.model.WalletBalance;
import com.example.runeforgemarket.wallet.model.WalletBalance.WalletBalanceId;
import com.example.runeforgemarket.wallet.model.WalletTransaction;
import com.example.runeforgemarket.wallet.repository.CurrencyRepository;
import com.example.runeforgemarket.wallet.repository.WalletBalanceRepository;
import com.example.runeforgemarket.wallet.repository.WalletRepository;
import com.example.runeforgemarket.wallet.repository.WalletTransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletBalanceRepository walletBalanceRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final CurrencyRepository currencyRepository;
    private final UserRepository userRepository;

    public WalletService(
        WalletRepository walletRepository,
        WalletBalanceRepository walletBalanceRepository,
        WalletTransactionRepository walletTransactionRepository,
        CurrencyRepository currencyRepository,
        UserRepository userRepository
    ) {
        this.walletRepository = walletRepository;
        this.walletBalanceRepository = walletBalanceRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.currencyRepository = currencyRepository;
        this.userRepository = userRepository;
    }

    public WalletBalanceResponse getBalance(Integer currencyId) {
        Wallet wallet = getOrCreateWallet();
        Currency currency = getCurrency(currencyId);

        WalletBalance balance = walletBalanceRepository
            .findById(new WalletBalanceId(wallet.getId(), currency.getId()))
            .orElseGet(() -> createZeroBalance(wallet, currency));

        return new WalletBalanceResponse(wallet.getId(), currency.getId(), balance.getBalance());
    }

    public List<WalletTransactionResponse> getTransactions(Integer currencyId) {
        Wallet wallet = getOrCreateWallet();
        Currency currency = getCurrency(currencyId);

        return walletTransactionRepository
            .findByWallet_IdAndCurrency_IdOrderByCreatedAtDesc(wallet.getId(), currency.getId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public WalletTransactionResponse addTransaction(CreateWalletTransactionRequest request) {
        Wallet wallet = getOrCreateWallet();
        Currency currency = getCurrency(request.currencyId());

        WalletBalance balance = walletBalanceRepository
            .findById(new WalletBalanceId(wallet.getId(), currency.getId()))
            .orElseGet(() -> createZeroBalance(wallet, currency));

        long newBalance = balance.getBalance() + request.amount();
        balance.setBalance(newBalance);
        walletBalanceRepository.save(balance);

        WalletTransaction tx = new WalletTransaction();
        tx.setWallet(wallet);
        tx.setCurrency(currency);
        tx.setAmount(request.amount());
        tx.setBalanceAfter(newBalance);
        tx.setType(request.type());
        tx.setRefType(request.refType());
        walletTransactionRepository.save(tx);

        return toResponse(tx);
    }

    private Wallet getOrCreateWallet() {
        User user = getCurrentUser();
        return walletRepository.findByUserId(user.getId())
            .orElseGet(() -> {
                Wallet wallet = new Wallet();
                wallet.setUserId(user.getId());
                return walletRepository.save(wallet);
            });
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Currency getCurrency(Integer currencyId) {
        return currencyRepository.findById(currencyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found"));
    }

    private WalletBalance createZeroBalance(Wallet wallet, Currency currency) {
        WalletBalance balance = new WalletBalance();
        balance.setId(new WalletBalanceId(wallet.getId(), currency.getId()));
        balance.setWallet(wallet);
        balance.setCurrency(currency);
        balance.setBalance(0L);
        return walletBalanceRepository.save(balance);
    }

    private WalletTransactionResponse toResponse(WalletTransaction tx) {
        return new WalletTransactionResponse(
            tx.getId(),
            tx.getWallet().getId(),
            tx.getCurrency().getId(),
            tx.getAmount(),
            tx.getBalanceAfter(),
            tx.getType(),
            tx.getRefType(),
            tx.getCreatedAt()
        );
    }
}
