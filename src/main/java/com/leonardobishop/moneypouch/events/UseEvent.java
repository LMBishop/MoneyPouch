package com.leonardobishop.moneypouch.events;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.Pouch;
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
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class UseEvent implements Listener {

    private MoneyPouch plugin;
    private ArrayList<UUID> opening = new ArrayList<>();

    public UseEvent(MoneyPouch plugin) {
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

                    if (opening.contains(player.getUniqueId())) {
                        player.sendMessage(plugin.getMessage(MoneyPouch.Message.ALREADY_OPENING));
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

    private void usePouch(Player player, Pouch p) {
        opening.add(player.getUniqueId());
        long random = ThreadLocalRandom.current().nextLong(p.getMinRange(), p.getMaxRange());
        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("pouches.sound.opensound")), 3, 1);
        new BukkitRunnable() {
            int position = 0;
            String prefixColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.prefix-colour"));
            String suffixColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.suffix-colour"));
            String revealColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.reveal-colour"));
            String obfuscateColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.obfuscate-colour"));
            String obfuscateDigitChar = plugin.getConfig().getString("pouches.title.obfuscate-digit-char", "#");
            String obfuscateDelimiterChar = ",";
            boolean delimiter = plugin.getConfig().getBoolean("pouches.title.format.enabled", false);
            boolean revealComma = plugin.getConfig().getBoolean("pouches.title.format.reveal-comma", false);
            String number = (delimiter ? (new DecimalFormat("#,###").format(random)) : String.valueOf(random));
            boolean reversePouchReveal = plugin.getConfig().getBoolean("reverse-pouch-reveal");

            @Override
            public void run() {
                if (!(plugin.getTitleHandle() instanceof Title_Other) && player.isOnline()) {
                    player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("pouches.sound.revealsound")), 3, 1);
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
                    opening.remove(player.getUniqueId());
                    this.cancel();
                    if (player.isOnline()) {
                        player.playSound(player.getLocation(), Sound.valueOf(plugin.getConfig().getString("pouches.sound.endsound")), 3, 1);
                        player.sendMessage(plugin.getMessage(MoneyPouch.Message.PRIZE_MESSAGE)
                                .replace("%prefix%", p.getEconomyType().getPrefix())
                                .replace("%suffix%", p.getEconomyType().getSuffix())
                                .replace("%prize%", String.valueOf(random)));
                    }
                    try {
                        p.getEconomyType().processPayment(player, random);
                    } catch (Throwable t) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to process payment for " + player.getName()
                                + " of amount " + random + " of economy " + p.getEconomyType().toString() + ", did they disconnect?");
                        t.printStackTrace();
                    }
                    return;
                }
                position++;
            }
        }.runTaskTimer(plugin, 10, plugin.getConfig().getInt("pouches.title.speed-in-tick"));
    }

}
