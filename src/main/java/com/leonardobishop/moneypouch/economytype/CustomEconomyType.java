package com.leonardobishop.moneypouch.economytype;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CustomEconomyType extends EconomyType {

    private final String command;

    public CustomEconomyType(String prefix, String suffix, String command) {
        super(prefix, suffix);
        this.command = command;
    }

    @Override
    public void processPayment(Player player, long amount) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
                command.replace("%player%", player.getName()).replace("%prize%", String.valueOf(amount)));
    }

    @Override
    public boolean doTransaction(Player player, long amount) {
        throw new RuntimeException("Cannot do shop transactions on custom YML economies.");
        // cannot do transactions for custom economies as there is no simple way
        // to check if the player has the required balance using commands
    }

    @Override
    public String toString() {
        return "Custom (/" + command + ")";
    }

}
