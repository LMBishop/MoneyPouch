package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.exceptions.PaymentFailedException;
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
            throw new PaymentFailedException("Player is offline!", new AssertionError("Player is offline!"));
        }
        int xp;
        try {
            xp = Integer.parseInt(String.valueOf(amount));
        } catch (NumberFormatException ex) {
            throw new PaymentFailedException("XP value is too large!");
        }
        player.giveExp(xp);
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
