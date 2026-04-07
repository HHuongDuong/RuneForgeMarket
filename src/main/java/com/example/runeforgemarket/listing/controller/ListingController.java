package com.example.runeforgemarket.listing.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.runeforgemarket.listing.dto.CreateListingRequest;
import com.example.runeforgemarket.listing.dto.ListingResponse;
import com.example.runeforgemarket.listing.service.ListingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

	private final ListingService listingService;

	public ListingController(ListingService listingService) {
		this.listingService = listingService;
	}

	@PostMapping
	public ListingResponse createListing(@Valid @RequestBody CreateListingRequest request) {
		return listingService.createListing(
			request.itemId(),
			request.currencyId(),
			request.price()
		);
	}

	@GetMapping
	public List<ListingResponse> getActiveListings(@RequestParam Integer currencyId) {
		return listingService.getActiveListings(currencyId);
	}

	@PostMapping("/{listingId}/cancel")
	public void cancelListing(@PathVariable Long listingId) {
		listingService.cancelListing(listingId);
	}

	@PostMapping("/{listingId}/lock")
	public void lockListing(@PathVariable Long listingId) {
		listingService.lockListing(listingId);
	}

	@PostMapping("/{listingId}/sold")
	public void markAsSold(@PathVariable Long listingId) {
		listingService.markAsSold(listingId);
	}
}
