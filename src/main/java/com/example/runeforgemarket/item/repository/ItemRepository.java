package com.example.runeforgemarket.item.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.runeforgemarket.item.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("""
        select i
        from Item i
        where i.owner.id = :ownerId
        """)
    List<Item> findAllByOwnerId(@Param("ownerId") Long ownerId);
}
