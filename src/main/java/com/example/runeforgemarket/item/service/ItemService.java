package com.example.runeforgemarket.item.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.runeforgemarket.item.dto.CreateItemRequest;
import com.example.runeforgemarket.item.dto.CreateItemTemplateRequest;
import com.example.runeforgemarket.item.dto.ItemResponse;
import com.example.runeforgemarket.item.model.Item;
import com.example.runeforgemarket.item.model.ItemTemplate;
import com.example.runeforgemarket.item.repository.ItemRepository;
import com.example.runeforgemarket.item.repository.ItemTemplateRepository;


@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemTemplateRepository itemTemplateRepository;


    public ItemService(ItemRepository itemRepository, ItemTemplateRepository itemTemplateRepository) {
        this.itemRepository = itemRepository;
        this.itemTemplateRepository = itemTemplateRepository;
    }

    public ItemResponse getItemById(Long id) {
        Item item = itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item not found"));

        return new ItemResponse(
            item.getId(),
            item.getTemplate().getName(),
            item.getOwnerId(),
            item.getTemplate().getType(),
            item.getTemplate().getRarity(),
            item.getStatus(),
            item.getStats()
        );
    }

    public List<ItemResponse> getOwnerItem(Long ownerId) {
        Item item = itemRepository.findByOwnerId(ownerId)
            .orElseThrow(() -> new RuntimeException("Item not found"));

        return List.of(new ItemResponse(
            item.getId(),
            item.getTemplate().getName(),
            item.getOwnerId(),
            item.getTemplate().getType(),
            item.getTemplate().getRarity(),
            item.getStatus(),
            item.getStats()
        ));
    }

    public List<ItemResponse> getItemByStatus(Long ownerId, String status) {
        List<Item> items = itemRepository.findByOwnerId(ownerId)
            .stream()
            .filter(item -> item.getStatus().name().equalsIgnoreCase(status))
            .toList();

        return items.stream()
            .map(item -> new ItemResponse(
                item.getId(),
                item.getTemplate().getName(),
                item.getOwnerId(),
                item.getTemplate().getType(),
                item.getTemplate().getRarity(),
                item.getStatus(),
                item.getStats()
            ))
            .toList();
    }

    public Item createItem(CreateItemRequest request) {
        // Logic to create item based on template and assign to owner
        Item item = new Item();
        item.setOwnerId(request.ownerId());
        // Set other properties based on templateId
        item.setTemplate(itemTemplateRepository.findById(request.templateId()).orElseThrow(() -> new RuntimeException("Template not found")));
        item.setStatus(request.status());
        item.setStats(randomItemStats(item));
        return itemRepository.save(item);
    }

    private Map<String, Object> randomItemStats(Item item) {
        // Logic to calculate item stats based on template and other factors
        Map<String, Object> baseStats = item.getTemplate().getBaseStats();
        if (baseStats == null || baseStats.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> finalStats = new HashMap<>();
        for (Map.Entry<String, Object> entry : baseStats.entrySet()) {
            // Apply random variation to base stats
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Number number) {
                int baseValue = number.intValue();
                if (item.getTemplate().getRarity() != null  && item.getTemplate().getStackable() != true) {
                    switch (item.getTemplate().getRarity()) {
                        case COMMON -> baseValue += (int) (Math.random() * 4); // Add 0-3 for common items
                        case RARE -> baseValue += (int) (Math.random() * 7) + 3; // Add 3-9 for rare items
                        case EPIC -> baseValue += (int) (Math.random() * 6) + 10; // Add 10-15 for epic items
                        case LEGENDARY -> baseValue += (int) (Math.random() * 5) + 16; // Add 16-20 for legendary items
                        default -> throw new RuntimeException("Unknown rarity: " + item.getTemplate().getRarity());
                    }
                }
                finalStats.put(key, baseValue);
                continue;
            }

            finalStats.put(key, value);
        }
        return finalStats;
    }

    public void transferItem(Long itemId, Long newOwnerId) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setOwnerId(newOwnerId);
        itemRepository.save(item);
    }

    public void deleteItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new RuntimeException("Item not found");
        }
        itemRepository.deleteById(itemId);
    }

    public ItemTemplate setItemTemplate(Integer templateId, CreateItemTemplateRequest request) {
        ItemTemplate itemTemplate = itemTemplateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found"));

        validateStats(request.baseStats());
        itemTemplate.setName(request.name());
        itemTemplate.setType(request.type());
        itemTemplate.setRarity(request.rarity());
        itemTemplate.setBaseStats(request.baseStats());
        itemTemplate.setIsNpcTradeable(request.isNpcTradeable());
        itemTemplate.setStackable(request.stackable());

        return itemTemplateRepository.save(itemTemplate);
    }

    public ItemTemplate createItemTemplate(CreateItemTemplateRequest request) {
        ItemTemplate itemTemplate = new ItemTemplate();
        validateStats(request.baseStats());
        itemTemplate.setName(request.name());
        itemTemplate.setType(request.type());
        itemTemplate.setRarity(request.rarity());
        itemTemplate.setBaseStats(request.baseStats());
        itemTemplate.setIsNpcTradeable(request.isNpcTradeable());
        itemTemplate.setStackable(request.stackable());

        return itemTemplateRepository.save(itemTemplate);
    }

    private void validateStats(Map<String, Object> stats) {
        if (stats == null || stats.isEmpty()) {
            throw new IllegalArgumentException("baseStats must not be empty");
        }

        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("baseStats contains an empty key");
            }

            if (!(value instanceof Number)) {
                throw new IllegalArgumentException("baseStats value must be a number for key: " + key);
            }

            double number = ((Number) value).doubleValue();
            if (Double.isNaN(number) || Double.isInfinite(number)) {
                throw new IllegalArgumentException("baseStats value is invalid for key: " + key);
            }

            if (number < 0) {
                throw new IllegalArgumentException("baseStats value must be >= 0 for key: " + key);
            }
        }
    }

    public ItemTemplate getItemTemplateById(Integer templateId) {
        return itemTemplateRepository.findById(templateId)
            .orElseThrow(() -> new RuntimeException("Template not found"));
    }

    public List<ItemTemplate> getAllItemTemplates() {
        return itemTemplateRepository.findAll();
    }

    public Item updateItem(Long itemId, CreateItemRequest request) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setOwnerId(request.ownerId());
        item.setTemplate(itemTemplateRepository.findById(request.templateId()).orElseThrow(() -> new RuntimeException("Template not found")));
        item.setStatus(request.status());
        return itemRepository.save(item);
    }
}
