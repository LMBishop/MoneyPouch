package com.leonardobishop.moneypouch;

import com.leonardobishop.moneypouch.commands.BaseCommand;
import com.leonardobishop.moneypouch.economytype.EconomyType;
import com.leonardobishop.moneypouch.economytype.LemonMobCoinsEconomyType;
import com.leonardobishop.moneypouch.economytype.VaultEconomyType;
import com.leonardobishop.moneypouch.economytype.XPEconomyType;
import com.leonardobishop.moneypouch.events.UseEvent;
import com.leonardobishop.moneypouch.title.Title;
import com.leonardobishop.moneypouch.title.Title_Bukkit;
import com.leonardobishop.moneypouch.title.Title_BukkitNoTimings;
import com.leonardobishop.moneypouch.title.Title_Other;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoneyPouch extends JavaPlugin {

    private Title titleHandle;

    private HashMap<String, EconomyType> economyTypes = new HashMap<>();

    public EconomyType getEconomyType(String id) {
        return economyTypes.get(id.toLowerCase());
    }

    public EconomyType registerEconomyType(String id, EconomyType type) {
        return economyTypes.put(id, type);
    }

    private ArrayList<Pouch> pouches = new ArrayList<>();

    @Override
    public void onEnable() {
        File directory = new File(String.valueOf(this.getDataFolder()));
        if (!directory.exists() && !directory.isDirectory()) {
            directory.mkdir();
        }

        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            try {
                config.createNewFile();
                try (InputStream in = MoneyPouch.class.getClassLoader().getResourceAsStream("config.yml")) {
                    OutputStream out = new FileOutputStream(config);
                    byte[] buffer = new byte[1024];
                    int length = in.read(buffer);
                    while (length != -1) {
                        out.write(buffer, 0, length);
                        length = in.read(buffer);
                    }
                } catch (IOException e) {
                    super.getLogger().severe("Failed to create config.");
                    e.printStackTrace();
                    super.getLogger().severe(ChatColor.RED + "...please delete the MoneyPouch directory and try RESTARTING (not reloading).");
                }
            } catch (IOException e) {
                super.getLogger().severe("Failed to create config.");
                e.printStackTrace();
                super.getLogger().severe(ChatColor.RED + "...please delete the MoneyPouch directory and try RESTARTING (not reloading).");
            }
        }
        this.setupTitle();

        registerEconomyType("xp", new XPEconomyType(this,   // vv for legacy purposes
                this.getConfig().getString("economy.xp.prefix", this.getConfig().getString("economy.prefixes.xp", "")),
                this.getConfig().getString("economy.xp.suffix", this.getConfig().getString("economy.suffixes.xp", " XP"))));


        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            registerEconomyType("vault", new VaultEconomyType(this,
                    this.getConfig().getString("economy.vault.prefix", this.getConfig().getString("economy.prefixes.vault", "$")),
                    this.getConfig().getString("economy.vault.suffix", this.getConfig().getString("economy.suffixes.vault", ""))));
        }

        if (Bukkit.getServer().getPluginManager().getPlugin("LemonMobCoins") != null) {
            registerEconomyType("lemonmobcoins", new LemonMobCoinsEconomyType(this,
                    this.getConfig().getString("economy.lemonmobcoins.prefix", ""),
                    this.getConfig().getString("economy.lemonmobcoins.suffix", " Mob Coins")));
        }

        super.getServer().getPluginCommand("moneypouch").setExecutor(new BaseCommand(this));
        super.getServer().getPluginManager().registerEvents(new UseEvent(this), this);

        Bukkit.getScheduler().runTask(this, this::reloadConfig);
    }

    public String getMessage(Message message) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("messages."
                + message.getId(), message.getDef()));
    }

    public ArrayList<Pouch> getPouches() {
        return pouches;
    }

    public Title getTitleHandle() {
        return titleHandle;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        pouches.clear();
        for (String s : this.getConfig().getConfigurationSection("pouches.tier").getKeys(false)) {
            ItemStack is = getItemStack("pouches.tier." + s, this.getConfig());
            String economyTypeId = this.getConfig().getString("pouches.tier." + s + ".options.economytype", "VAULT");
            long priceMin = this.getConfig().getLong("pouches.tier." + s + ".pricerange.from", 0);
            long priceMax = this.getConfig().getLong("pouches.tier." + s + ".pricerange.to", 0);

            EconomyType economyType = getEconomyType(economyTypeId);
            if (economyType == null) economyType = getEconomyType("VAULT");

            pouches.add(new Pouch(s.replace(" ", "_"), priceMin, priceMax, is, economyType));
        }
    }

    public ItemStack getItemStack(String path, FileConfiguration config) {
        String cName = config.getString(path + ".name", path + ".name");
        String cType = config.getString(path + ".item", path + ".item");
        List<String> cLore = config.getStringList(path + ".lore");

        String name;
        Material type = null;
        int data = 0;
        List<String> lore = new ArrayList<>();
        if (cLore != null) {
            for (String s : cLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
        name = ChatColor.translateAlternateColorCodes('&', cName);

        if (StringUtils.isNumeric(cType)) {
            type = Material.getMaterial(Integer.parseInt(cType));
        } else if (Material.getMaterial(cType) != null) {
            type = Material.getMaterial(cType);
        } else if (cType.contains(":")) {
            String[] parts = cType.split(":");
            if (parts.length > 1) {
                if (StringUtils.isNumeric(parts[0])) {
                    type = Material.getMaterial(Integer.parseInt(parts[0]));
                } else if (Material.getMaterial(parts[0]) != null) {
                    type = Material.getMaterial(parts[0]);
                }
                if (StringUtils.isNumeric(parts[1])) {
                    data = Integer.parseInt(parts[1]);
                }
            }
        }

        if (type == null) {
            type = Material.STONE;
        }


        ItemStack is = new ItemStack(type, 1, (short) data);
        ItemMeta ism = is.getItemMeta();
        ism.setLore(lore);
        ism.setDisplayName(name);
        is.setItemMeta(ism);

        if (config.isSet(path + ".enchantments")) {
            for (String key : getConfig().getStringList(path + ".enchantments")) {
                String[] split = key.split(":");
                String ench = split[0];
                String level;
                if (split.length == 2) {
                    level = split[1];
                } else {
                    level = "1";
                }

                if (Enchantment.getByName(ench) == null) continue;

                try {
                    Integer.parseInt(level);
                } catch (NumberFormatException e) {
                    level = "1";
                }

                is.addUnsafeEnchantment(Enchantment.getByName(ench), Integer.parseInt(level));
            }
        }

        return is;
    }

    private void setupTitle() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            titleHandle = new Title_Bukkit();
            this.getLogger().info("Your server version could not be detected. Titles have been enabled, although they may not work!");
            return;
        }
        getLogger().info("Your server is running version " + version + ".");
        if (version.startsWith("v1_7")) {
            titleHandle = new Title_Other();
        } else if (version.startsWith("v1_8") || version.startsWith("v1_9") || version.startsWith("v1_10")) {
            titleHandle = new Title_BukkitNoTimings();
        } else {
            titleHandle = new Title_Bukkit();
        }
        if (titleHandle instanceof Title_Bukkit) {
            this.getLogger().info("Titles have been enabled.");
        } else if (titleHandle instanceof Title_BukkitNoTimings) {
            this.getLogger().info("Titles have been enabled, although they have limited timings.");
        } else {
            this.getLogger().info("Titles are not supported for this version.");
        }
    }

    public enum Message {

        FULL_INV("full-inv", "&c%player%'s inventory is full!"),
        GIVE_ITEM("give-item", "&6Given &e%player% %item%&6."),
        RECEIVE_ITEM("receive-item", "&6You have been given %item%&6."),
        PRIZE_MESSAGE("prize-message", "&6You have received &c%prefix%%prize%%suffix%&6!"),
        ALREADY_OPENING("already-opening", "&cPlease wait for your current pouch opening to complete first!");

        private String id;
        private String def; // (default message if undefined)

        Message(String id, String def) {
            this.id = id;
            this.def = def;
        }

        public String getId() {
            return id;
        }

        public String getDef() {
            return def;
        }
    }

}
