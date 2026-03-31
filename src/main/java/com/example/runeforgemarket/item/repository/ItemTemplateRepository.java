package com.example.runeforgemarket.item.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.runeforgemarket.item.model.ItemTemplate;

public interface ItemTemplateRepository extends JpaRepository<ItemTemplate, Long> {

    Optional<ItemTemplate> findById(Integer templateId);
    
}
