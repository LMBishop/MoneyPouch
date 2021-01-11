package com.leonardobishop.moneypouch.gui;

import com.leonardobishop.moneypouch.MoneyPouch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class MenuController implements Listener {

    private final HashMap<Player, Menu> menuTracker = new HashMap<>();

    public MenuController(MoneyPouch plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Menu menu : menuTracker.values()) {
                menu.update();
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (menuTracker.containsKey(player)) {
            event.setCancelled(true);
            menuTracker.get(player).onClick(event.getSlot());
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (menuTracker.containsKey(player)) {
            Menu menu = menuTracker.get(player);
            Menu superMenu = menu.getSuperMenu();
            if (superMenu != null) {
                openMenu(player, menu);
            } else {
                menuTracker.remove(player);
            }
        }
    }

    public void closeMenu(Player player) {
        menuTracker.remove(player);
        player.closeInventory();
    }

    public void openMenu(Player player, Menu menu) {
        menu.initialize();
        menu.update();
        menuTracker.put(player, menu);
        player.openInventory(menu.getInventory());
    }

}
