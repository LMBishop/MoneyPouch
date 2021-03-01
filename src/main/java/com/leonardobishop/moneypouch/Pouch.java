package com.leonardobishop.moneypouch;

import com.leonardobishop.moneypouch.economytype.EconomyType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a type of pouch on the server.
 */
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

    /**
     * Boolean of whether or not a permission is required to open the pouch.
     *
     * @return boolean
     */
    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    /**
     * Boolean of whether or not the pouch can be bought from the MoneyPouch shop.
     *
     * @return boolean
     */
    public boolean isPurchasable() {
        return purchasable;
    }

    /**
     * Get the {@link EconomyType} of the currency used to purchase the pouch from the MoneyPouch shop.
     * Returns {@code null} if no currency is specified.
     *
     * @return {@link EconomyType}
     */
    public EconomyType getPurchaseCurrency() {
        return purchaseCurrency;
    }

    /**
     * Get the price of the pouch in the MoneyPouch shop.
     *
     * @return long
     */
    public long getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Get the unique ID of the pouch.
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Get the lower bound of the possible rewards from this pouch.
     *
     * @return long
     */
    public long getMinRange() {
        return minRange;
    }

    /**
     * Get the upper bound of the possible rewards from this pouch.
     *
     * @return long
     */
    public long getMaxRange() {
        return maxRange;
    }

    /**
     * Get the {@link ItemStack} used to represent this pouch in the player's inventory.
     *
     * @return {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the {@link ItemStack} used to represent this pouch in the MoneyPouch shop.
     *
     * @return {@link ItemStack}
     */
    public ItemStack getShopItemStack() {
        return shopItemStack;
    }

    /**
     * Get the {@link EconomyType} of the currency used as a reward from this pouch.
     *
     * @return {@link EconomyType}
     */
    public EconomyType getEconomyType() {
        return economyType;
    }
}
