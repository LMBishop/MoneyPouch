package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.exceptions.PaymentFailedException;
import org.bukkit.entity.Player;

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
