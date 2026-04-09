package com.example.runeforgemarket.listing.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateListingRequest(
    @NotNull Long itemId,
    @NotNull Integer currencyId,
    @NotNull @Positive Long price
) {
}
