package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.MoneyPouch;
import me.max.lemonmobcoins.bukkit.LemonMobCoinsBukkitPlugin;
import me.max.lemonmobcoins.common.LemonMobCoins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class LemonMobCoinsEconomyType extends EconomyType {

    private final MoneyPouch plugin;
    private boolean fail = false;
    private LemonMobCoins lemonMobCoinsClass;

    public LemonMobCoinsEconomyType(MoneyPouch plugin, String prefix, String suffix) {
        super(prefix, suffix);
        this.plugin = plugin;

        // TODO API for LemonMobCoins seems broken, using reflection instead
        try {
            LemonMobCoinsBukkitPlugin mobCoinsPlugin = (LemonMobCoinsBukkitPlugin) Bukkit.getPluginManager().getPlugin("LemonMobCoins");
            Field mobCoins = mobCoinsPlugin.getClass().getDeclaredField("lemonMobCoins");
            mobCoins.setAccessible(true);
            lemonMobCoinsClass = (LemonMobCoins) mobCoins.get(mobCoinsPlugin);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail = true;
            plugin.getLogger().log(Level.SEVERE, "Cannot hook into LemonMobCoins!");
            e.printStackTrace();
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("LemonMobCoins") == null) {
            fail = true;
            plugin.getLogger().log(Level.SEVERE, "Plugin 'LemonMobCoins' is not present on the server!");
        }
    }
    @Override
    public void processPayment(Player player, long amount) {
        if (fail) {
            plugin.getLogger().log(Level.SEVERE, "Failed to process payment: failed to hook into LemonMobCoins");
            return;
        }

        try {
            lemonMobCoinsClass.getCoinManager().addCoinsToPlayer(player.getUniqueId(), amount);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to process payment: an unknown exception occurred");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LemonMobCoins";
    }

}
