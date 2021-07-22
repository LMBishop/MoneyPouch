package com.leonardobishop.moneypouch.listener;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.Pouch;
import com.leonardobishop.moneypouch.economytype.InvalidEconomyType;
import com.leonardobishop.moneypouch.title.Title_Other;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class UseListener implements Listener {

    protected final MoneyPouch plugin;
    protected final Set<UUID> opening = new HashSet<>();

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
            onRightClickInMainHand(player, event);
        }
    }

    protected void onRightClickInMainHand(Player player, Cancellable event) {
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

    protected void playSound(Player player, String name) {
        try {
            player.playSound(player.getLocation(), Sound.valueOf(name), 3, 1);
        } catch (Exception ignored) { }
    }

    protected void usePouch(Player player, Pouch pouch) {
        long random = ThreadLocalRandom.current().nextLong(pouch.getMinRange(), pouch.getMaxRange());
        playSound(player, plugin.getConfig().getString("pouches.sound.opensound"));

        PaymentRunnable paymentRunnable = new PaymentRunnable(plugin, random, player, pouch);
        if (plugin.getTitleHandle() instanceof Title_Other) {
            paymentRunnable.pay();
        } else {
            paymentRunnable.runTaskTimer(plugin, 10, plugin.getConfig().getInt("pouches.title.speed-in-tick"));
        }
    }

    private class PaymentRunnable extends BukkitRunnable {

        private final Player player;
        private final Pouch pouch;
        private final long payment;

        private final String prefixColour;
        private final String suffixColour;
        private final String revealColour;
        private final String obfuscateColour;
        private final String obfuscateDigitChar;
        private final String obfuscateDelimiterChar;
        private final boolean delimiter;
        private final boolean revealComma;
        private final String number;
        private final boolean reversePouchReveal;

        private int position;
        private boolean paid;

        public PaymentRunnable(MoneyPouch plugin, long payment, Player player, Pouch pouch) {
            opening.add(player.getUniqueId());

            this.player = player;
            this.payment = payment;
            this.pouch = pouch;

            this.prefixColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.prefix-colour"));
            this.suffixColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.suffix-colour"));
            this.revealColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.reveal-colour"));
            this.obfuscateColour = ChatColor.translateAlternateColorCodes('&',
                    plugin.getConfig().getString("pouches.title.obfuscate-colour"));
            this.obfuscateDigitChar = plugin.getConfig().getString("pouches.title.obfuscate-digit-char", "#");
            this.obfuscateDelimiterChar = ",";
            this.delimiter = plugin.getConfig().getBoolean("pouches.title.format.enabled", false);
            this.revealComma = plugin.getConfig().getBoolean("pouches.title.format.reveal-comma", false);
            this.number = (delimiter ? (new DecimalFormat("#,###").format(payment)) : String.valueOf(payment));
            this.reversePouchReveal = plugin.getConfig().getBoolean("reverse-pouch-reveal");
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                stop();
                return;
            }

            playSound(player, plugin.getConfig().getString("pouches.sound.revealsound"));
            String prefix = prefixColour + pouch.getEconomyType().getPrefix();
            StringBuilder viewedTitle = new StringBuilder();
            String suffix = suffixColour + pouch.getEconomyType().getSuffix();
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
            plugin.getTitleHandle().sendTitle(player, prefix + viewedTitle + suffix,
                    ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("pouches.title.subtitle")));
            position++;

            if (position > number.length()) {
                stop();
            }
        }

        public void stop() {
            this.cancel();
            pay();
        }

        public void pay() {
            if (paid) throw new IllegalStateException("player already paid!"); // prevent me from myself
            this.paid = true;

            opening.remove(player.getUniqueId());

            boolean success = false;
            // the payout is wrapped in a try-catch block such that if it fails
            // the error is logged and payments can be issued manually by operators
            try {
                pouch.getEconomyType().processPayment(player, payment);
                success = true;
            } catch (Throwable t) {
                if (plugin.getConfig().getBoolean("error-handling.log-failed-transactions", true)) {
                    plugin.getLogger().log(Level.SEVERE, "Failed to process payment from pouch with ID '" + pouch.getId() + "' for player '" + player.getName()
                            + "' of amount " + payment + " of economy " + pouch.getEconomyType().toString() + ": " + t.getMessage());
                }
                if (player.isOnline()) {
                    if (plugin.getConfig().getBoolean("error-handling.refund-pouch", false)) {
                        player.getInventory().addItem(pouch.getItemStack());
                    }
                    player.sendMessage(plugin.getMessage(MoneyPouch.Message.REWARD_ERROR)
                            .replace("%prefix%", pouch.getEconomyType().getPrefix())
                            .replace("%suffix%", pouch.getEconomyType().getSuffix())
                            .replace("%prize%", NumberFormat.getInstance().format(payment)));
                }
                t.printStackTrace();
            }
            if (success) {
                if (player.isOnline()) {
                    playSound(player, plugin.getConfig().getString("pouches.sound.endsound"));
                    player.sendMessage(plugin.getMessage(MoneyPouch.Message.PRIZE_MESSAGE)
                            .replace("%prefix%", pouch.getEconomyType().getPrefix())
                            .replace("%suffix%", pouch.getEconomyType().getSuffix())
                            .replace("%prize%", NumberFormat.getInstance().format(payment)));
                }
            }
        }
    }
}
