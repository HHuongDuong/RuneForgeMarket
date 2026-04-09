package com.example.runeforgemarket.listing.dto;

public record ListingResponse(
    Long id,
    Long sellerId,
    String itemName,
    String itemDescription,
    String status,
    Long price,
    String currencyName
) {
    
}
