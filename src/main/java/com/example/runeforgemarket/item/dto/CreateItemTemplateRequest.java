package com.example.runeforgemarket.item.dto;

import java.util.Map;

import com.example.runeforgemarket.item.model.enums.Rarity;
import com.example.runeforgemarket.item.model.enums.Type;

public record CreateItemTemplateRequest(
        String name,
        Type type,
        Rarity rarity,
        Map<String, Object> baseStats,
        Boolean isNpcTradeable,
        Boolean stackable
) {
}
