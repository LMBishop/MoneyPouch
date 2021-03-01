package com.leonardobishop.moneypouch.economytype;

import org.bukkit.entity.Player;

public abstract class EconomyType {

    private final String prefix;
    private final String suffix;

    public EconomyType(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    /**
     * Process the reward for a player when they open a MoneyPouch.
     *
     * @param player the player to receive the reward
     * @param amount the reward amount (prize)
     */
    public abstract void processPayment(Player player, long amount);

    /**
     * Charge the player when they purchase a MoneyPouch from the shop.
     *
     * @param player the player purchasing a MoneyPouch
     * @param amount the amount to be charged
     * @return true if the payment succeeds, false if otherwise
     */
    public abstract boolean doTransaction(Player player, long amount);

    public abstract String toString();

}
