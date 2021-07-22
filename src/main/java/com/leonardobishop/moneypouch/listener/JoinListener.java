package com.leonardobishop.moneypouch.listener;

import com.leonardobishop.moneypouch.MoneyPouch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final MoneyPouch plugin;

    public JoinListener(MoneyPouch plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getUpdater().isUpdateReady() && event.getPlayer().hasPermission("moneypouch.admin")) {
            // delay for a bit so they actually see the message
            String updateMessage = ChatColor.LIGHT_PURPLE + "A new version "
                    + ChatColor.DARK_PURPLE + plugin.getUpdater().getReturnedVersion()
                    + ChatColor.LIGHT_PURPLE + " was found (your version: "
                    + ChatColor.DARK_PURPLE + plugin.getUpdater().getReturnedVersion()
                    + ChatColor.LIGHT_PURPLE + "). Please update me! <3 - Link: "
                    + ChatColor.UNDERLINE + plugin.getUpdater().getUpdateLink();
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getPlayer().sendMessage(updateMessage), 50L);
        }
    }

}
