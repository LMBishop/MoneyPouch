package com.leonardobishop.moneypouch;

import com.leonardobishop.moneypouch.economytype.EconomyType;
import org.bukkit.inventory.ItemStack;

public class Pouch {

    private final String id;
    private final long minRange;
    private final long maxRange;
    private final ItemStack itemStack;
    private final EconomyType economyType;

    public Pouch(String id, long minRange, long maxRange, ItemStack itemStack, EconomyType economyType) {
        this.id = id;
        if (minRange >= maxRange) {
            this.minRange = maxRange - 1;
        } else {
            this.minRange = minRange;
        }
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
