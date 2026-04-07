package com.example.runeforgemarket.listing.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.runeforgemarket.common.currency.model.Currency;
import com.example.runeforgemarket.common.currency.service.CurrencyService;
import com.example.runeforgemarket.item.model.Item;
import com.example.runeforgemarket.item.repository.ItemRepository;
import com.example.runeforgemarket.listing.dto.ListingResponse;
import com.example.runeforgemarket.listing.model.Listing;
import com.example.runeforgemarket.listing.model.ListingPrice;
import com.example.runeforgemarket.listing.model.ListingPrice.ListingPriceId;
import com.example.runeforgemarket.listing.model.Status;
import com.example.runeforgemarket.listing.repository.ListingPriceRepository;
import com.example.runeforgemarket.listing.repository.ListingRepository;
import com.example.runeforgemarket.user.model.User;
import com.example.runeforgemarket.user.service.CurrentUserService;

import jakarta.transaction.Transactional;

@Service
public class ListingService {
    private final ListingRepository listingRepository;
    private final ListingPriceRepository listingPriceRepository;
    private final ItemRepository itemRepository;
    private final CurrencyService currencyService;
    private final CurrentUserService currentUserService;

    public ListingService(
        ListingRepository listingRepository,
        ListingPriceRepository listingPriceRepository,
        ItemRepository itemRepository,
        CurrencyService currencyService,
        CurrentUserService currentUserService
    ) {
        this.listingRepository = listingRepository;
        this.listingPriceRepository = listingPriceRepository;
        this.itemRepository = itemRepository;
        this.currencyService = currencyService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ListingResponse createListing(Long itemId, Integer currencyId, Long price) {
        if (price == null || price <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
        }

        User seller = currentUserService.getCurrentUser();
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        if (!seller.getId().equals(item.getOwner().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Item does not belong to seller");
        }

        Currency currency = currencyService.getCurrency(currencyId);

        Listing listing = new Listing();
        listing.setSeller(seller);
        listing.setItem(item);
        listing.setStatus(Status.ACTIVE);
        listing = listingRepository.save(listing);

        ListingPrice listingPrice = new ListingPrice();
        listingPrice.setId(new ListingPriceId(listing.getId(), currency.getId()));
        listingPrice.setListing(listing);
        listingPrice.setCurrency(currency);
        listingPrice.setPrice(price);
        listingPriceRepository.save(listingPrice);

        return toResponse(listing, listingPrice);
    }

    public List<ListingResponse> getActiveListings(Integer currencyId) {
        Currency currency = currencyService.getCurrency(currencyId);
        return listingRepository.findByStatus(Status.ACTIVE).stream()
            .map(listing -> {
                ListingPrice price = listingPriceRepository
                    .findByListingIdAndCurrencyId(listing.getId(), currency.getId());
                if (price == null) {
                    return null;
                }
                return toResponse(listing, price);
            })
            .filter(response -> response != null)
            .toList();
    }

    @Transactional
    public void cancelListing(Long listingId) {
        Listing listing = getListing(listingId);
        User seller = currentUserService.getCurrentUser();
        if (!listing.getSeller().getId().equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Listing does not belong to seller");
        }

        if (listing.getStatus() == Status.SOLD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing already sold");
        }

        listing.setStatus(Status.CANCELLED);
        listingRepository.save(listing);
    }

    @Transactional
    public void lockListing(Long listingId) {
        Listing listing = getListing(listingId);
        if (listing.getStatus() != Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Listing not available");
        }

        listing.setStatus(Status.LOCKED);
        listingRepository.save(listing);
    }

    @Transactional
    public void markAsSold(Long listingId) {
        Listing listing = getListing(listingId);
        if (listing.getStatus() != Status.LOCKED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Listing must be locked before sold");
        }

        listing.setStatus(Status.SOLD);
        listingRepository.save(listing);
    }

    public Long getPrice(Long listingId, Integer currencyId) {
        ListingPrice price = listingPriceRepository.findByListingIdAndCurrencyId(listingId, currencyId);
        if (price == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Price not found for currency");
        }
        return price.getPrice();
    }

    public Long getSellerId(Long listingId) {
        return getListing(listingId).getSeller().getId();
    }

    public Long getItemId(Long listingId) {
        return getListing(listingId).getItem().getId();
    }

    private Listing getListing(Long listingId) {
        return listingRepository.findById(listingId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));
    }

    private ListingResponse toResponse(Listing listing, ListingPrice price) {
        return new ListingResponse(
            listing.getId(),
            listing.getSeller().getId(),
            listing.getItem().getTemplate().getName(),
            null,
            listing.getStatus().name(),
            price.getPrice(),
            price.getCurrency().getName().name()
        );
    }
}
