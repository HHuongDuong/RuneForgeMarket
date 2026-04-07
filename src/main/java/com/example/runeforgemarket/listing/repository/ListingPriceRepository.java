package com.example.runeforgemarket.listing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.listing.model.ListingPrice;
import com.example.runeforgemarket.listing.model.ListingPrice.ListingPriceId;

public interface ListingPriceRepository extends JpaRepository<ListingPrice, ListingPriceId> {
    @Query("""
        select lp
        from ListingPrice lp
        where lp.listing.id = :listingId
        """)
    List<ListingPrice> findByListingId(@Param("listingId") Long listingId);

    @Query("""
        select lp
        from ListingPrice lp
        where lp.listing.id = :listingId
          and lp.currency.id = :currencyId
        """)
    ListingPrice findByListingIdAndCurrencyId(
        @Param("listingId") Long listingId,
        @Param("currencyId") Integer currencyId
    );
}
