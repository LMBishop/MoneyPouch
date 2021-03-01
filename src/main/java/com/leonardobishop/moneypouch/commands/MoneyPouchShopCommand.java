package com.leonardobishop.moneypouch.commands;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.gui.ShopMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyPouchShopCommand implements CommandExecutor {

    private final MoneyPouch plugin;

    public MoneyPouchShopCommand(MoneyPouch plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (!plugin.getConfig().getBoolean("shop.enabled", false)) {
                sender.sendMessage(plugin.getMessage(MoneyPouch.Message.SHOP_DISABLED));
                return true;
            }
            Player player = (Player) sender;
            plugin.getMenuController().openMenu(player, new ShopMenu(player, plugin));
        }
        return true;
    }

}
