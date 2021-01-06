package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.MoneyPouch;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class VaultEconomyType extends EconomyType {

    private final MoneyPouch plugin;
    private static Economy economy = null;
    private boolean fail = false;

    public VaultEconomyType(MoneyPouch plugin, String prefix, String suffix) {
        super(prefix, suffix);
        this.plugin = plugin;

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
            plugin.getLogger().log(Level.SEVERE, "Failed to process payment: failed to hook into Vault");
            return;
        }

        economy.depositPlayer(player, amount);
    }

    @Override
    public String toString() {
        return "Vault";
    }

}
