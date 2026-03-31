package com.example.runeforgemarket.item.dto;

import java.util.Map;

import com.example.runeforgemarket.item.model.enums.Rarity;
import com.example.runeforgemarket.item.model.enums.Type;
import com.example.runeforgemarket.item.model.enums.Status;

public record ItemResponse(
    Long id,
    String name,
    Long ownerId,
    Type type,
    Rarity rarity,
    Status status,
    Map<String, Object> stats
) {
}