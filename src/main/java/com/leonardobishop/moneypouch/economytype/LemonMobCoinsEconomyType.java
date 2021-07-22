package com.leonardobishop.moneypouch.economytype;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.exception.PaymentFailedException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public class LemonMobCoinsEconomyType extends EconomyType {

    private final MoneyPouch plugin;

    private boolean fail = false;
    private Object mobCoinsBukkitPlugin;
    private Object lemonMobCoinsCoinManagerObject;
    private Method addCoinsToPlayerMethod;
    private Method getCoinsOfPlayerMethod;
    private Method deductCoinsFromPlayerMethod;

    public LemonMobCoinsEconomyType(MoneyPouch plugin, String prefix, String suffix) {
        super(prefix, suffix);
        this.plugin = plugin;

        reflect();

        if (Bukkit.getServer().getPluginManager().getPlugin("LemonMobCoins") == null) {
            fail = true;
            plugin.getLogger().log(Level.SEVERE, "Plugin 'LemonMobCoins' is not present on the server!");
        }
    }
    @Override
    public void processPayment(Player player, long amount) {
        // this can change if lemonmobcoins is reloaded using plugman as some weird fuckery happens
        if (mobCoinsBukkitPlugin.getClass().getName().equals(
                Bukkit.getPluginManager().getPlugin("LemonMobCoins").getClass().getName())) {
            reflect();
        }

        if (fail) {
            throw new PaymentFailedException("Failed to hook into LemonMobCoins!");
        }
        try {
            addCoinsToPlayerMethod.invoke(lemonMobCoinsCoinManagerObject,
                    player.getUniqueId(), Double.parseDouble(String.valueOf(amount)));
        } catch (Exception e) {
            throw new PaymentFailedException("An unknown exception occurred", e);
        }
    }

    private void reflect() {
        // TODO API for LemonMobCoins seems broken, using reflection instead
        try {
            mobCoinsBukkitPlugin = Bukkit.getPluginManager().getPlugin("LemonMobCoins");
            Field mobCoinsField = mobCoinsBukkitPlugin.getClass().getDeclaredField("lemonMobCoins");
            mobCoinsField.setAccessible(true);
            Object lemonMobCoinsObject = mobCoinsField.get(mobCoinsBukkitPlugin);
            Field coinManagerField = lemonMobCoinsObject.getClass().getDeclaredField("coinManager");
            coinManagerField.setAccessible(true);
            lemonMobCoinsCoinManagerObject = coinManagerField.get(lemonMobCoinsObject);
            addCoinsToPlayerMethod = lemonMobCoinsCoinManagerObject.getClass()
                    .getDeclaredMethod("addCoinsToPlayer", UUID.class, double.class);
            addCoinsToPlayerMethod.setAccessible(true);
            getCoinsOfPlayerMethod = lemonMobCoinsCoinManagerObject.getClass()
                    .getDeclaredMethod("getCoinsOfPlayer", UUID.class);
            getCoinsOfPlayerMethod.setAccessible(true);
            deductCoinsFromPlayerMethod = lemonMobCoinsCoinManagerObject.getClass()
                    .getDeclaredMethod("deductCoinsFromPlayer", UUID.class, double.class);
            deductCoinsFromPlayerMethod.setAccessible(true);
        } catch (Exception e) {
            fail = true;
            plugin.getLogger().log(Level.SEVERE, "Cannot hook into LemonMobCoins!");
            e.printStackTrace();
        }
    }

    @Override
    public boolean doTransaction(Player player, long amount) {
        if (fail) {
            return false;
        }

        try {
            if ((double) getCoinsOfPlayerMethod.invoke(lemonMobCoinsCoinManagerObject, player.getUniqueId()) < amount) {
                return false;
            }
            deductCoinsFromPlayerMethod.invoke(lemonMobCoinsCoinManagerObject, player.getUniqueId(), amount);
            return true;
        } catch (Exception e) {
            throw new PaymentFailedException("An unknown exception occurred", e);
        }
    }

    @Override
    public String toString() {
        return "LemonMobCoins";
    }

}
