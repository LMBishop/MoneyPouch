package com.leonardobishop.moneypouch.economytype;

import org.bukkit.entity.Player;

public abstract class EconomyType {

    private String prefix;
    private String suffix;

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

    public abstract void processPayment(Player player, long amount);
    public abstract String toString();

}
