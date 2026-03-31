package com.example.runeforgemarket.item.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.runeforgemarket.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByOwnerId(Long ownerId);
    
}
