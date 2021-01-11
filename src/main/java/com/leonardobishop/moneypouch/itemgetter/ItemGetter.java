package com.leonardobishop.moneypouch.itemgetter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public interface ItemGetter {

    ItemStack getItem(String path, FileConfiguration config, JavaPlugin plugin);

}
