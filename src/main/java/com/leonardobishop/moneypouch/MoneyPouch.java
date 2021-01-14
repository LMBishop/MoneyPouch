package com.leonardobishop.moneypouch;

import com.leonardobishop.moneypouch.commands.MoneyPouchBaseCommand;
import com.leonardobishop.moneypouch.commands.MoneyPouchShopCommand;
import com.leonardobishop.moneypouch.economytype.EconomyType;
import com.leonardobishop.moneypouch.economytype.LemonMobCoinsEconomyType;
import com.leonardobishop.moneypouch.economytype.VaultEconomyType;
import com.leonardobishop.moneypouch.economytype.XPEconomyType;
import com.leonardobishop.moneypouch.events.UseEvent;
import com.leonardobishop.moneypouch.gui.MenuController;
import com.leonardobishop.moneypouch.itemgetter.ItemGetter;
import com.leonardobishop.moneypouch.itemgetter.ItemGetterLatest;
import com.leonardobishop.moneypouch.itemgetter.ItemGetter_1_13;
import com.leonardobishop.moneypouch.itemgetter.ItemGetter_Late_1_8;
import com.leonardobishop.moneypouch.title.Title;
import com.leonardobishop.moneypouch.title.Title_Bukkit;
import com.leonardobishop.moneypouch.title.Title_BukkitNoTimings;
import com.leonardobishop.moneypouch.title.Title_Other;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoneyPouch extends JavaPlugin {

    private Title titleHandle;
    private ItemGetter itemGetter;
    private MenuController menuController;

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
        executeVersionSpecificActions();

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

        MetricsLite metrics = new MetricsLite(this, 9927);
        if (metrics.isEnabled()) {
            super.getLogger().info("Metrics started. This can be disabled at /plugins/bStats/config.yml.");
        }

        menuController = new MenuController(this);

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

        super.getServer().getPluginCommand("moneypouch").setExecutor(new MoneyPouchBaseCommand(this));
        super.getServer().getPluginCommand("moneypouchshop").setExecutor(new MoneyPouchShopCommand(this));
        super.getServer().getPluginManager().registerEvents(new UseEvent(this), this);
        super.getServer().getPluginManager().registerEvents(menuController, this);

        Bukkit.getScheduler().runTask(this, this::reload);
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

    private void executeVersionSpecificActions() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            getLogger().warning("Failed to resolve server version - some features will not work!");
            itemGetter = new ItemGetter_Late_1_8();
            return;
        }

        getLogger().info("Your server is running version " + version + ".");
        if (version.startsWith("v1_7") || version.startsWith("v1_8") || version.startsWith("v1_9")
                || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")) {
            itemGetter = new ItemGetter_Late_1_8();
        } else if (version.startsWith("v1_13")) {
            itemGetter = new ItemGetter_1_13();
        } else {
            itemGetter = new ItemGetterLatest();
        }
    }

    public MenuController getMenuController() {
        return menuController;
    }

    public void reload() {
        super.reloadConfig();

        pouches.clear();
        for (String s : this.getConfig().getConfigurationSection("pouches.tier").getKeys(false)) {
            ItemStack is = getItemStack("pouches.tier." + s, this.getConfig());
            String economyTypeId = this.getConfig().getString("pouches.tier." + s + ".options.economytype", "VAULT");
            String id = s.replace(" ", "_");
            long priceMin = this.getConfig().getLong("pouches.tier." + s + ".pricerange.from", 0);
            long priceMax = this.getConfig().getLong("pouches.tier." + s + ".pricerange.to", 0);
            boolean permissionRequired = this.getConfig().getBoolean("pouches.tier." + s + ".options.permission-required", false);

            EconomyType economyType = getEconomyType(economyTypeId);
            if (economyType == null) economyType = getEconomyType("VAULT");

            //shop
            boolean purchasable = this.getConfig().contains("shop.purchasable-items." + s);
            if (purchasable) {
                long price = this.getConfig().getLong("shop.purchasable-items." + s + ".price", 0);
                String purchaseEconomyId = this.getConfig().getString("shop.purchasable-items." + s + ".currency", "VAULT");
                EconomyType purchaseEconomy = getEconomyType(purchaseEconomyId);

                if (purchaseEconomy == null) purchaseEconomy = getEconomyType("VAULT");

                ItemStack shopIs = getItemStack("pouches.tier." + s, this.getConfig());
                ItemMeta shopIsm = shopIs.getItemMeta();
                List<String> shopIsLore = new ArrayList<>();
                if (shopIsm.getLore() != null) {
                    shopIsLore.addAll(shopIsm.getLore());
                }
                for (String shopLore : this.getConfig().getStringList("shop.append-to-lore")) {
                    shopIsLore.add(ChatColor.translateAlternateColorCodes('&', shopLore)
                            .replace("%price%", String.valueOf(price))
                            .replace("%prefix%", purchaseEconomy.getPrefix())
                            .replace("%suffix%", purchaseEconomy.getSuffix()));
                }
                shopIsm.setLore(shopIsLore);
                shopIs.setItemMeta(shopIsm);

                pouches.add(new Pouch(s.replace(" ", "_"), priceMin, priceMax, is, economyType, permissionRequired, purchasable, purchaseEconomy, price, shopIs));
            } else {
                pouches.add(new Pouch(s.replace(" ", "_"), priceMin, priceMax, is, economyType, permissionRequired));
            }
        }
    }

    public ItemStack getItemStack(String path, FileConfiguration config) {
        return itemGetter.getItem(path, config, this);
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
        ALREADY_OPENING("already-opening", "&cPlease wait for your current pouch opening to complete first!"),
        INVENTORY_FULL("inventory-full", "&cYour inventory is full"),
        PURCHASE_SUCCESS("purchase-success", "&6You have purchased %item%&6 for &c%prefix%%price%%suffix%&6."),
        PURCHASE_FAIL("purchase-fail", "&cYou do not have &c%prefix%%price%%suffix%&6."),
        SHOP_DISABLED("shop-disabled", "&cThe pouch shop is disabled."),
        NO_PERMISSION("no-permission", "&cYou cannot open this pouch.");

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
