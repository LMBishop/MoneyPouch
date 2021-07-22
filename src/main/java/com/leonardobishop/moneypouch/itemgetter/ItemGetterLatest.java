package com.leonardobishop.moneypouch.itemgetter;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemGetterLatest implements ItemGetter {

    private Field profileField;

    /*
     supporting:
      - name
      - material
      - lore
      - enchantments (NamespacedKey)
       - itemflags
      - unbreakable
      - attribute modifier
      - custom model data

      requires at least API version 1.14
      */
    @Override
    public ItemStack getItem(String path, FileConfiguration config, JavaPlugin plugin) {
        String cName = config.getString(path + ".name", path + ".name");
        String cType = config.getString(path + ".item", path + ".item");
        boolean hasCustomModelData = config.contains(path + ".custommodeldata");
        int customModelData = config.getInt(path + ".custommodeldata", 0);
        boolean unbreakable = config.getBoolean(path + ".unbreakable", false);
        List<String> cLore = config.getStringList(path + ".lore");
        List<String> cItemFlags = config.getStringList(path + ".itemflags");
        boolean hasAttributeModifiers = config.contains(path + ".attributemodifiers");
        List<Map<?, ?>> cAttributeModifiers = config.getMapList(path + ".attributemodifiers");

        String name;
        Material type = null;
        int data = 0;

        // lore
        List<String> lore = new ArrayList<>();
        if (cLore != null) {
            for (String s : cLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }
        }

        // name
        name = ChatColor.translateAlternateColorCodes('&', cName);

        // material
        try {
            type = Material.valueOf(cType);
        } catch (Exception e) {
            plugin.getLogger().warning("Unrecognised material: " + cType);
            type = Material.STONE;
        }

        ItemStack is = new ItemStack(type, 1, (short) data);
        ItemMeta ism = is.getItemMeta();

        // skull
        if (is.getType() == Material.PLAYER_HEAD) {
            SkullMeta sm = (SkullMeta) ism;
            String cOwnerBase64 = config.getString(path + "owner-base64");
            String cOwnerUsername = config.getString(path + "owner-username");
            String cOwnerUuid = config.getString(path + "owner-uuid");
            if (cOwnerBase64 != null || cOwnerUsername != null || cOwnerUuid != null) {
                if (cOwnerUsername != null) {
                    sm.setOwner(cOwnerUsername);
                } else if (cOwnerUuid != null) {
                    try {
                        sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(cOwnerUuid)));
                    } catch (IllegalArgumentException ignored) { }
                } else {
                    GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                    profile.getProperties().put("textures", new Property("textures", cOwnerBase64));
                    if (profileField == null) {
                        try {
                            profileField = sm.getClass().getDeclaredField("profile");
                            profileField.setAccessible(true);
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        profileField.set(sm, profile);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        ism.setLore(lore);
        ism.setDisplayName(name);

        // custom model data
        if (hasCustomModelData) {
            ism.setCustomModelData(customModelData);
        }

        // attribute modifiers
        if (hasAttributeModifiers) {
            for (Map<?, ?> attr : cAttributeModifiers) {
                String cAttribute = (String) attr.get("attribute");
                Attribute attribute = null;
                for (Attribute enumattr : Attribute.values()) {
                    if (enumattr.toString().equals(cAttribute)) {
                        attribute = enumattr;
                        break;
                    }
                }

                if (attribute == null) continue;

                Map<?, ?> configurationSection = (Map<?, ?>) attr.get("modifier");

                String cUUID = (String) configurationSection.get("uuid");
                String cModifierName = (String) configurationSection.get("name");
                String cModifierOperation = (String) configurationSection.get("operation");
                double cAmount;
                try {
                    Object cAmountObj = configurationSection.get("amount");
                    if (cAmountObj instanceof Integer) {
                        cAmount = ((Integer) cAmountObj).doubleValue();
                    } else {
                        cAmount = (Double) cAmountObj;
                    }
                } catch (Exception e) {
                    cAmount = 1;
                }
                String cEquipmentSlot = (String) configurationSection.get("equipmentslot");

                UUID uuid = null;
                if (cUUID != null) {
                    try {
                        uuid = UUID.fromString(cUUID);
                    } catch (Exception ignored) {
                        // ignored
                    }
                }
                EquipmentSlot equipmentSlot = null;
                if (cEquipmentSlot != null) {
                    try {
                        equipmentSlot = EquipmentSlot.valueOf(cEquipmentSlot);
                    } catch (Exception ignored) {
                        // ignored
                    }
                }
                AttributeModifier.Operation operation = AttributeModifier.Operation.ADD_NUMBER;
                try {
                    operation = AttributeModifier.Operation.valueOf(cModifierOperation);
                } catch (Exception ignored) {
                    // ignored
                }

                AttributeModifier modifier;
                if (uuid == null) {
                    modifier = new AttributeModifier(cModifierName, cAmount, operation);
                } else if (equipmentSlot == null) {
                    modifier = new AttributeModifier(uuid, cModifierName, cAmount, operation);
                } else {
                    modifier = new AttributeModifier(uuid, cModifierName, cAmount, operation, equipmentSlot);
                }

                ism.addAttributeModifier(attribute, modifier);
            }
        }

        // item flags
        if (config.isSet(path + ".itemflags")) {
            for (String flag : cItemFlags) {
                for (ItemFlag iflag : ItemFlag.values()) {
                    if (iflag.toString().equals(flag)) {
                        ism.addItemFlags(iflag);
                        break;
                    }
                }
            }
        }


        // unbreakable
        ism.setUnbreakable(unbreakable);

        // enchantments
        if (config.isSet(path + ".enchantments")) {
            for (String key : config.getStringList(path + ".enchantments")) {
                String[] split = key.split(":");
                if (split.length < 2) {
                    plugin.getLogger().warning("Enchantment does not follow format {namespace}:{name}:{level} : " + key);
                    continue;
                }
                String namespace = split[0];
                String ench = split[1];
                String levelName;
                if (split.length >= 3) {
                    levelName = split[2];
                } else {
                    levelName = "1";
                }

                NamespacedKey namespacedKey;
                try {
                    namespacedKey = new NamespacedKey(namespace, ench);
                } catch (Exception e) {
                    plugin.getLogger().warning("Unrecognised namespace: " + namespace);
                    continue;
                }
                Enchantment enchantment;
                if ((enchantment = Enchantment.getByKey(namespacedKey)) == null) {
                    plugin.getLogger().warning("Unrecognised enchantment: " + namespacedKey);
                    continue;
                }

                int level;
                try {
                    level = Integer.parseInt(levelName);
                } catch (NumberFormatException e) {
                    level = 1;
                }

                ism.addEnchant(enchantment, level, true);
            }
        }

        is.setItemMeta(ism);
        return is;
    }
}
