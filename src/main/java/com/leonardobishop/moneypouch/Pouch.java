package com.leonardobishop.moneypouch;

import com.leonardobishop.moneypouch.economytype.EconomyType;
import org.bukkit.inventory.ItemStack;

public class Pouch {

    private final String id;
    private final long minRange;
    private final long maxRange;
    private final ItemStack itemStack;
    private final EconomyType economyType;
    private final boolean purchasable;
    private final EconomyType purchaseCurrency;
    private final long purchasePrice;
    private final ItemStack shopItemStack;
    private final boolean permissionRequired;

    public Pouch(String id, long minRange, long maxRange, ItemStack itemStack, EconomyType economyType, boolean permissionRequired) {
        this.id = id;
        if (minRange >= maxRange) {
            this.minRange = maxRange - 1;
        } else {
            this.minRange = minRange;
        }
        this.maxRange = maxRange;
        this.itemStack = itemStack;
        this.economyType = economyType;
        this.permissionRequired = permissionRequired;
        this.purchasable = false;
        this.purchaseCurrency = null;
        this.purchasePrice = 0;
        this.shopItemStack = null;
    }

    public Pouch(String id, long minRange, long maxRange, ItemStack itemStack, EconomyType economyType, boolean permissionRequired,
                 boolean purchasable, EconomyType purchaseCurrency, long purchasePrice, ItemStack shopItemStack) {
        this.id = id;
        if (minRange >= maxRange) {
            this.minRange = maxRange - 1;
        } else {
            this.minRange = minRange;
        }
        this.maxRange = maxRange;
        this.itemStack = itemStack;
        this.economyType = economyType;
        this.permissionRequired = permissionRequired;
        this.purchasable = purchasable;
        this.purchaseCurrency = purchaseCurrency;
        this.purchasePrice = purchasePrice;
        this.shopItemStack = shopItemStack;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public EconomyType getPurchaseCurrency() {
        return purchaseCurrency;
    }

    public long getPurchasePrice() {
        return purchasePrice;
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

    public ItemStack getShopItemStack() {
        return shopItemStack;
    }

    public EconomyType getEconomyType() {
        return economyType;
    }
}
