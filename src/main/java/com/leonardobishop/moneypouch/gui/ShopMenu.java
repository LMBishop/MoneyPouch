package com.leonardobishop.moneypouch.gui;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.Pouch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopMenu extends Menu {

    private final MoneyPouch plugin;
    private final HashMap<Integer, Pouch> pouchLocations = new HashMap<>();

    public ShopMenu(Player player, MoneyPouch plugin) {
        super(player);
        this.plugin = plugin;
    }

    @Override
    public void onClick(int slot) {
        Pouch pouch = pouchLocations.get(slot);
        if (super.getInventory().firstEmpty() == -1) {
            super.getPlayer().sendMessage(plugin.getMessage(MoneyPouch.Message.INVENTORY_FULL));
        } else if (pouch != null) {
            boolean success;
            try {
                success = pouch.getPurchaseCurrency().doTransaction(super.getPlayer(), pouch.getPurchasePrice());
            } catch (Exception e) {
                super.getPlayer().sendMessage(plugin.getMessage(MoneyPouch.Message.PURCHASE_ERROR)
                        .replace("%item%", pouch.getItemStack().getItemMeta().getDisplayName())
                        .replace("%prefix%", pouch.getPurchaseCurrency().getPrefix())
                        .replace("%suffix%", pouch.getPurchaseCurrency().getSuffix())
                        .replace("%price%", String.valueOf(pouch.getPurchasePrice())));
                return;
            }
            if (success) {
                super.getPlayer().getInventory().addItem(pouch.getItemStack());
                super.getPlayer().sendMessage(plugin.getMessage(MoneyPouch.Message.PURCHASE_SUCCESS)
                        .replace("%item%", pouch.getItemStack().getItemMeta().getDisplayName())
                        .replace("%prefix%", pouch.getPurchaseCurrency().getPrefix())
                        .replace("%suffix%", pouch.getPurchaseCurrency().getSuffix())
                        .replace("%price%", String.valueOf(pouch.getPurchasePrice())));
            } else {
                super.getPlayer().sendMessage(plugin.getMessage(MoneyPouch.Message.PURCHASE_FAIL)
                        .replace("%prefix%", pouch.getPurchaseCurrency().getPrefix())
                        .replace("%suffix%", pouch.getPurchaseCurrency().getSuffix())
                        .replace("%price%", String.valueOf(pouch.getPurchasePrice())));
            }
        }
    }

    @Override
    public void update() {
        // not implementeed
    }

    @Override
    public void initialize() {
        List<Pouch> purchasablePouches = new ArrayList<>();
        for (Pouch p : plugin.getPouches()) {
            if (p.isPurchasable()) {
                purchasablePouches.add(p);
            }
        }
        int max = purchasablePouches.size();
        int size;
        if (max <= 0) {
            size =  9;
        } else {
            int quotient = (int) Math.ceil(max / 9.0);
            size = (quotient > 5 ? 54 : quotient * 9);
        }

        super.setInventory(Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("shop.ui-title", "Pouch Shop"))));

        int slot = 0;
        for (Pouch p : purchasablePouches) {
            pouchLocations.put(slot, p);
            super.getInventory().setItem(slot, p.getShopItemStack());
            slot++;
        }
    }
}
