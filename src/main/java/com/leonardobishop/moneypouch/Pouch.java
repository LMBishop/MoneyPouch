package com.leonardobishop.moneypouch;

import com.leonardobishop.moneypouch.economytype.EconomyType;
import org.bukkit.inventory.ItemStack;

public class Pouch {

    private String id;
    private long minRange;
    private long maxRange;
    private ItemStack itemStack;
    private EconomyType economyType;

    public Pouch(String id, long minRange, long maxRange, ItemStack itemStack, EconomyType economyType) {
        this.id = id;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.itemStack = itemStack;
        this.economyType = economyType;
    }

    public String getId() {
        return id;
    }

    public long getMinRange() {
        return minRange;
    }

    public long getMaxRange() {
        return maxRange;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public EconomyType getEconomyType() {
        return economyType;
    }
}
