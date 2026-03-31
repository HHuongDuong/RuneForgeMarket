package com.example.runeforgemarket.item.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.runeforgemarket.item.model.enums.Rarity;
import com.example.runeforgemarket.item.model.enums.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "item_templates")
public class ItemTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "type", nullable = false)
    private Type type; //enum

    @Column(name = "rarity", nullable = false)
    private Rarity rarity; //enum

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "base_stats", columnDefinition = "jsonb")
    private Map<String, Object> baseStats;

    @Column(name = "is_npc_tradeable", nullable = false)
    private Boolean isNpcTradeable;

    @Column(name = "istackable", nullable = false)
    private Boolean stackable;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public Map<String, Object> getBaseStats() {
        return baseStats;
    }

    public void setBaseStats(Map<String, Object> baseStats) {
        this.baseStats = baseStats;
    }

    public Boolean getIsNpcTradeable() {
        return isNpcTradeable;
    }

    public void setIsNpcTradeable(Boolean isNpcTradeable) {
        this.isNpcTradeable = isNpcTradeable;
    }

    public Boolean getStackable() {
        return stackable;
    }

    public void setStackable(Boolean stackable) {
        this.stackable = stackable;
    }

    
}
