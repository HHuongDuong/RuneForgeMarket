package com.example.runeforgemarket.listing.model;

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
@Table(name = "listing_prices")
public class ListingPrice {

    @EmbeddedId
    private ListingPriceId id;

    @MapsId("listingId")
    @ManyToOne(optional=false)
    @JoinColumn(name="listing_id", nullable=false)
    private Listing listing;
    
    @MapsId("currencyId")
    @ManyToOne(optional=false)
    @JoinColumn(name="currency_id", nullable=false)
    private Currency currency;

    @Column(name="price", nullable=false)
    private Long price;
    public Listing getListing() {
        return listing;
    }
    public void setListing(Listing listing) {
        this.listing = listing;
    }
    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }

    public ListingPriceId getId() {
        return id;
    }

    public void setId(ListingPriceId id) {
        this.id = id;
    }

    @Embeddable
    public static class ListingPriceId implements Serializable {
        @Column(name = "listing_id", nullable = false)
        private Long listingId;

        @Column(name = "currency_id", nullable = false)
        private Integer currencyId;

        public ListingPriceId() {
        }

        public ListingPriceId(Long listingId, Integer currencyId) {
            this.listingId = listingId;
            this.currencyId = currencyId;
        }

        public Long getListingId() {
            return listingId;
        }

        public void setListingId(Long listingId) {
            this.listingId = listingId;
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
            ListingPriceId that = (ListingPriceId) o;
            return Objects.equals(listingId, that.listingId)
                && Objects.equals(currencyId, that.currencyId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(listingId, currencyId);
        }
    }

    
}
