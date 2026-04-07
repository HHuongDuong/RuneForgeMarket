package com.example.runeforgemarket.listing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.listing.model.Listing;
import com.example.runeforgemarket.listing.model.Status;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    @Query("""
        select l
        from Listing l
        where l.status = :status
        """)
    List<Listing> findByStatus(@Param("status") Status status);
}
