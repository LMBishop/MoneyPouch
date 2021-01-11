package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.MoneyPouch;
import org.bukkit.entity.Player;

public class XPEconomyType extends EconomyType {

    private final MoneyPouch plugin;

    public XPEconomyType(MoneyPouch plugin, String prefix, String suffix) {
        super(prefix, suffix);
        this.plugin = plugin;
    }

    @Override
    public void processPayment(Player player, long amount) {
        if (!player.isOnline()) {
            throw new AssertionError("Player is offline!");
        }
        player.giveExp(Integer.parseInt(String.valueOf(amount)));
    }

    @Override
    public boolean doTransaction(Player player, long amount) {
        if (player.getTotalExperience() < amount) {
            return false;
        }
        player.giveExp(Integer.parseInt(String.valueOf(amount)) * -1);
        return true;
    }

    @Override
    public String toString() {
        return "XP";
    }

}
