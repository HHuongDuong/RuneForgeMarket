package com.example.runeforgemarket.wallet.model;

import java.io.Serializable;
import java.util.Objects;

import com.example.runeforgemarket.common.currency.model.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallet_balance")
public class WalletBalance {

    @EmbeddedId
    private WalletBalanceId id;

    @MapsId("walletId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @MapsId("currencyId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @Column(name = "balance", nullable = false)
    private Long balance;

    public WalletBalanceId getId() {
        return id;
    }

    public void setId(WalletBalanceId id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Embeddable
    public static class WalletBalanceId implements Serializable {
        @Column(name = "wallet_id", nullable = false)
        private Long walletId;

        @Column(name = "currency_id", nullable = false)
        private Integer currencyId;

        public WalletBalanceId() {
        }

        public WalletBalanceId(Long walletId, Integer currencyId) {
            this.walletId = walletId;
            this.currencyId = currencyId;
        }

        public Long getWalletId() {
            return walletId;
        }

        public void setWalletId(Long walletId) {
            this.walletId = walletId;
        }

        public Integer getCurrencyId() {
            return currencyId;
        }

        public void setCurrencyId(Integer currencyId) {
            this.currencyId = currencyId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            WalletBalanceId that = (WalletBalanceId) o;
            return Objects.equals(walletId, that.walletId)
                && Objects.equals(currencyId, that.currencyId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(walletId, currencyId);
        }
    }
}
