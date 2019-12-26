package com.leonardobishop.moneypouch.economytype;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class VaultEconomyType extends EconomyType {

    private static Economy economy = null;
    private boolean fail = false;

    public VaultEconomyType(String prefix, String suffix) {
        super(prefix, suffix);

        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            fail = true;
            Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().log(Level.SEVERE, "Failed to hook Vault!");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            fail = true;
            Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().log(Level.SEVERE, "Failed to hook Vault!");
            return;
        }
        economy = rsp.getProvider();
    }

    @Override
    public void processPayment(Player player, long amount) {
        if (fail) {
            Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().log(Level.SEVERE, "Failed to process payment: Failed to hook Vault!");
            return;
        }

        economy.depositPlayer(player, amount);
    }

    @Override
    public String toString() {
        return "Vault";
    }

}
