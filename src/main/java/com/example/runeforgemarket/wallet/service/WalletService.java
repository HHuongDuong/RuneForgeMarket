package com.example.runeforgemarket.wallet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.runeforgemarket.common.currency.model.Currency;
import com.example.runeforgemarket.common.currency.service.CurrencyService;
import com.example.runeforgemarket.user.model.User;
import com.example.runeforgemarket.user.service.CurrentUserService;
import com.example.runeforgemarket.wallet.dto.CreateWalletTransactionRequest;
import com.example.runeforgemarket.wallet.dto.WalletBalanceResponse;
import com.example.runeforgemarket.wallet.dto.WalletTransactionResponse;
import com.example.runeforgemarket.wallet.model.Wallet;
import com.example.runeforgemarket.wallet.model.WalletBalance;
import com.example.runeforgemarket.wallet.model.WalletBalance.WalletBalanceId;
import com.example.runeforgemarket.wallet.model.WalletTransaction;
import com.example.runeforgemarket.wallet.model.TransactionType;
import com.example.runeforgemarket.wallet.repository.WalletBalanceRepository;
import com.example.runeforgemarket.wallet.repository.WalletRepository;
import com.example.runeforgemarket.wallet.repository.WalletTransactionRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletBalanceRepository walletBalanceRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final CurrentUserService currentUserService;
    private final CurrencyService currencyService;

    public WalletService(
        WalletRepository walletRepository,
        WalletBalanceRepository walletBalanceRepository,
        WalletTransactionRepository walletTransactionRepository,
        CurrentUserService currentUserService,
        CurrencyService currencyService
    ) {
        this.walletRepository = walletRepository;
        this.walletBalanceRepository = walletBalanceRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.currentUserService = currentUserService;
        this.currencyService = currencyService;
    }

    @Transactional
    public WalletBalanceResponse getBalance(Integer currencyId) {
        Wallet wallet = getOrCreateWallet();
        Currency currency = currencyService.getCurrency(currencyId);

        Map<Integer, WalletBalance> balanceMap = walletBalanceRepository
            .findByWalletId(wallet.getId())
            .stream()
            .collect(Collectors.toMap(
                wb -> wb.getCurrency().getId(),
                wb -> wb
            ));

        WalletBalance balance = balanceMap.get(currency.getId());
        if (balance == null) {
            balance = createZeroBalance(wallet, currency);
        }

        return new WalletBalanceResponse(wallet.getId(), currency.getId(), balance.getBalance());
    }

    @Transactional
    public List<WalletBalanceResponse> getBalances() {
        Wallet wallet = getOrCreateWallet();
        Map<Integer, WalletBalance> balanceMap = walletBalanceRepository
            .findByWalletId(wallet.getId())
            .stream()
            .collect(Collectors.toMap(
                wb -> wb.getCurrency().getId(),
                wb -> wb
            ));

        List<WalletBalance> newBalances = new ArrayList<>();
        List<WalletBalanceResponse> responses = currencyService.getAllCurrencies().stream()
            .map(currency -> {
                WalletBalance balance = balanceMap.get(currency.getId());
                if (balance == null) {
                    balance = buildZeroBalance(wallet, currency);
                    newBalances.add(balance);
                }

                return new WalletBalanceResponse(
                    balance.getWallet().getId(),
                    balance.getCurrency().getId(),
                    balance.getBalance()
                );
            })
            .toList();

        if (!newBalances.isEmpty()) {
            walletBalanceRepository.saveAll(newBalances);
        }

        return responses;
    }

    public List<WalletTransactionResponse> getTransactions(Integer currencyId) {
        Wallet wallet = getOrCreateWallet();
        if (currencyId == null) {
            return walletTransactionRepository.findByWallet(wallet.getId())
                .stream()
                .map(this::toResponse)
                .toList();
        }

        Currency currency = currencyService.getCurrency(currencyId);
        return walletTransactionRepository.findByWalletAndCurrency(wallet.getId(), currency.getId())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public WalletTransactionResponse applyTransaction(CreateWalletTransactionRequest request) {
        if (request.amount() == null || request.amount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        if (request.type() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction type is required");
        }

        Wallet wallet = getOrCreateWallet();
        Currency currency = currencyService.getCurrency(request.currencyId());

        WalletBalance balance = walletBalanceRepository
            .findById(new WalletBalanceId(wallet.getId(), currency.getId()))
            .orElseGet(() -> createZeroBalance(wallet, currency));

        if (request.type() == TransactionType.DEBIT && balance.getBalance() < request.amount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        long delta = calculateDelta(request.type(), request.amount());
        long newBalance = balance.getBalance() + delta;
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
        User user = currentUserService.getCurrentUser();
        return walletRepository.findWalletByUserId(user.getId())
            .orElseGet(() -> {
                Wallet wallet = new Wallet();
                wallet.setUserId(user.getId());
                return walletRepository.save(wallet);
            });
    }

    private WalletBalance createZeroBalance(Wallet wallet, Currency currency) {
        WalletBalance balance = new WalletBalance();
        balance.setId(new WalletBalanceId(wallet.getId(), currency.getId()));
        balance.setWallet(wallet);
        balance.setCurrency(currency);
        balance.setBalance(0L);
        return walletBalanceRepository.save(balance);
    }

    private WalletBalance buildZeroBalance(Wallet wallet, Currency currency) {
        WalletBalance balance = new WalletBalance();
        balance.setId(new WalletBalanceId(wallet.getId(), currency.getId()));
        balance.setWallet(wallet);
        balance.setCurrency(currency);
        balance.setBalance(0L);
        return balance;
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

    private long calculateDelta(TransactionType type, long amount) {
        if (type == TransactionType.DEBIT) {
            return -amount;
        }

        if (type == TransactionType.ADJUST) {
            return amount;
        }

        return amount;
    }
}
