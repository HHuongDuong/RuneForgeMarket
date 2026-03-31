package com.example.runeforgemarket.item.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.runeforgemarket.item.dto.CreateItemRequest;
import com.example.runeforgemarket.item.dto.CreateItemTemplateRequest;
import com.example.runeforgemarket.item.dto.ItemResponse;
import com.example.runeforgemarket.item.model.Item;
import com.example.runeforgemarket.item.model.ItemTemplate;
import com.example.runeforgemarket.item.service.ItemService;


@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{id}")
    public ItemResponse getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping("/owner/{ownerId}")
    public List<ItemResponse> getOwnerItem(@PathVariable Long ownerId) {
        return itemService.getOwnerItem(ownerId);
    }

    @GetMapping("/owner/{ownerId}/status/{status}")
    public List<ItemResponse> getItemByStatus(@PathVariable Long ownerId, @PathVariable String status) {
        return itemService.getItemByStatus(ownerId, status);
    }

    @PostMapping("/create_item")
    public Item createItem(@RequestBody CreateItemRequest request) {
         return itemService.createItem(request);
    }

    @PutMapping("/update_item/{itemId}")
    public Item updateItem(@PathVariable Long itemId, @RequestBody CreateItemRequest request) {
        return itemService.updateItem(itemId, request);
    }

    @GetMapping("/template/{templateId}")
    public ItemTemplate getItemTemplateById(@PathVariable Integer templateId) {
        return itemService.getItemTemplateById(templateId);
    }

    @GetMapping("/template")
    public List<ItemTemplate> getAllItemTemplates() {
        return itemService.getAllItemTemplates();
    }

    @PostMapping("/create_template")
    public ItemTemplate createItemTemplate(@RequestBody CreateItemTemplateRequest request) {
        return itemService.createItemTemplate(request);
    }

    @PutMapping("/update_template/{templateId}")
    public ItemTemplate updateItemTemplate(@PathVariable Integer templateId, @RequestBody CreateItemTemplateRequest request) {
        return itemService.setItemTemplate(templateId, request);
    }

}
