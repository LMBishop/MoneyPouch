package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.exceptions.PaymentFailedException;
import org.bukkit.entity.Player;

/**
 * Represents an invalid economy type.
 * The plugin will default to using this type if an invalid economy ID has been entered in the config.
 */
public class InvalidEconomyType extends EconomyType {

    public InvalidEconomyType() {
        super("", "");
    }

    @Override
    public void processPayment(Player player, long amount) {
        throw new PaymentFailedException("Invalid economy type!");
    }

    @Override
    public boolean doTransaction(Player player, long amount) {
        throw new PaymentFailedException("Invalid economy type!");
    }

    @Override
    public String toString() {
        return "Invalid";
    }

}
