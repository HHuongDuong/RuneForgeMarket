package com.example.runeforgemarket.item.dto;

import com.example.runeforgemarket.item.model.enums.Status;

public record CreateItemRequest(
    Long ownerId,
    Integer templateId,
    Status status
) {  
}
