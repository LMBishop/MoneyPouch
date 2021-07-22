package com.leonardobishop.moneypouch.listener;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.Pouch;
import com.leonardobishop.moneypouch.economytype.InvalidEconomyType;
import com.leonardobishop.moneypouch.title.Title_Other;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class UseListener implements Listener {

    protected final MoneyPouch plugin;
    protected final ArrayList<UUID> opening = new ArrayList<>();

    public UseListener(MoneyPouch plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
            for (Pouch p : plugin.getPouches()) {
                if (p.getItemStack().isSimilar(player.getItemInHand())) {
                    event.setCancelled(true);

                    if (p.getEconomyType() instanceof InvalidEconomyType
                            && plugin.getConfig().getBoolean("error-handling.prevent-opening-invalid-pouches", true)) {
                        player.sendMessage(plugin.getMessage(MoneyPouch.Message.INVALID_POUCH));
                        return;
                    }

                    if (opening.contains(player.getUniqueId())) {
                        player.sendMessage(plugin.getMessage(MoneyPouch.Message.ALREADY_OPENING));
                        return;
                    }

                    String permission = "moneypouch.pouches." + p.getId();
                    if (p.isPermissionRequired() && !player.hasPermission(permission)) {
                        player.sendMessage(plugin.getMessage(MoneyPouch.Message.NO_PERMISSION));
                        return;
                    }

                    if (player.getItemInHand().getAmount() == 1) {
                        player.setItemInHand(null);
                    } else {
                        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                        player.updateInventory();
                    }

                    usePouch(player, p);
                }
            }
        }
    }

    protected void playSound(Player player, String name) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(name), 3, 1);
        } catch (Exception ignored) { }
    }

    protected void usePouch(Player player, Pouch p) {
        opening.add(player.getUniqueId());
        long random = ThreadLocalRandom.current().nextLong(p.getMinRange(), p.getMaxRange());
        playSound(player, plugin.getConfig().getString("pouches.sound.opensound"));
        new BukkitRunnable() {
            final String prefixColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.prefix-colour"));
            final String suffixColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.suffix-colour"));
            final String revealColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.reveal-colour"));
            final String obfuscateColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.obfuscate-colour"));
            final String obfuscateDigitChar = plugin.getConfig().getString("pouches.title.obfuscate-digit-char", "#");
            final String obfuscateDelimiterChar = ",";
            final boolean delimiter = plugin.getConfig().getBoolean("pouches.title.format.enabled", false);
            final boolean revealComma = plugin.getConfig().getBoolean("pouches.title.format.reveal-comma", false);
            final String number = (delimiter ? (new DecimalFormat("#,###").format(random)) : String.valueOf(random));
            final boolean reversePouchReveal = plugin.getConfig().getBoolean("reverse-pouch-reveal");

            int position = 0;
            boolean complete = false;

            @Override
            public void run() {
                if (!(plugin.getTitleHandle() instanceof Title_Other) && player.isOnline()) {
                    playSound(player, plugin.getConfig().getString("pouches.sound.revealsound"));
                    String prefix = prefixColour + p.getEconomyType().getPrefix();
                    StringBuilder viewedTitle = new StringBuilder();
                    String suffix = suffixColour + p.getEconomyType().getSuffix();
                    for (int i = 0; i < position; i++) {
                        if (reversePouchReveal) {
                            viewedTitle.insert(0, number.charAt(number.length() - i - 1)).insert(0, revealColour);
                        } else {
                            viewedTitle.append(revealColour).append(number.charAt(i));
                        }
                        if ((i == (position - 1)) && (position != number.length())
                                && (reversePouchReveal
                                ? (revealComma && (number.charAt(number.length() - i - 1)) == ',')
                                : (revealComma && (number.charAt(i + 1)) == ','))) {
                            position++;
                        }
                    }
                    for (int i = position; i < number.length(); i++) {
                        if (reversePouchReveal) {
                            char at = number.charAt(number.length() - i - 1);
                            if (at == ',')  {
                                if (revealComma) {
                                    viewedTitle.insert(0, at).insert(0, revealColour);
                                } else viewedTitle.insert(0, obfuscateDelimiterChar).insert(0, ChatColor.MAGIC).insert(0, obfuscateColour);
                            } else viewedTitle.insert(0, obfuscateDigitChar).insert(0, ChatColor.MAGIC).insert(0, obfuscateColour);;
                        } else {
                            char at = number.charAt(i);
                            if (at == ',') {
                                if (revealComma) viewedTitle.append(revealColour).append(at);
                                else viewedTitle.append(obfuscateColour).append(ChatColor.MAGIC).append(obfuscateDelimiterChar);
                            } else viewedTitle.append(obfuscateColour).append(ChatColor.MAGIC).append(obfuscateDigitChar);
                        }
                    }
                    plugin.getTitleHandle().sendTitle(player, prefix + viewedTitle.toString() + suffix,
                            ChatColor.translateAlternateColorCodes('&',
                                    plugin.getConfig().getString("pouches.title.subtitle")));
                } else {
                    position = number.length();
                }
                if (position == number.length()) {
                    if (complete) {    // prevent running twice
                        return;
                    }
                    complete = true;

                    opening.remove(player.getUniqueId());
                    this.cancel();
                    try {
                        p.getEconomyType().processPayment(player, random);
                        if (player.isOnline()) {
                            playSound(player, plugin.getConfig().getString("pouches.sound.endsound"));
                            player.sendMessage(plugin.getMessage(MoneyPouch.Message.PRIZE_MESSAGE)
                                    .replace("%prefix%", p.getEconomyType().getPrefix())
                                    .replace("%suffix%", p.getEconomyType().getSuffix())
                                    .replace("%prize%", NumberFormat.getInstance().format(random)));
                        }
                    } catch (Throwable t) {
                        if (plugin.getConfig().getBoolean("error-handling.log-failed-transactions", true)) {
                            plugin.getLogger().log(Level.SEVERE, "Failed to process payment from pouch with ID '" + p.getId() + "' for player '" + player.getName()
                                    + "' of amount " + random + " of economy " + p.getEconomyType().toString() + ": " + t.getMessage());
                        }
                        if (player.isOnline()) {
                            if (plugin.getConfig().getBoolean("error-handling.refund-pouch", false)) {
                                player.getInventory().addItem(p.getItemStack());
                            }
                            player.sendMessage(plugin.getMessage(MoneyPouch.Message.REWARD_ERROR)
                                    .replace("%prefix%", p.getEconomyType().getPrefix())
                                    .replace("%suffix%", p.getEconomyType().getSuffix())
                                    .replace("%prize%", NumberFormat.getInstance().format(random)));
                        }
                        t.printStackTrace();
                    }
                    return;
                }
                position++;
            }
        }.runTaskTimer(plugin, 10, plugin.getConfig().getInt("pouches.title.speed-in-tick"));
    }

}
