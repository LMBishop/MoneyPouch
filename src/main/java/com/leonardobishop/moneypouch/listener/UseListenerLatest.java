package com.leonardobishop.moneypouch.listener;

import com.leonardobishop.moneypouch.MoneyPouch;
import com.leonardobishop.moneypouch.Pouch;
import com.leonardobishop.moneypouch.economytype.InvalidEconomyType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;


public class UseEventLatest extends UseEvent implements Listener {

    public UseEventLatest(MoneyPouch plugin) {
        super(plugin);
    }

    @EventHandler
    @Override
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (event.getHand() == EquipmentSlot.HAND) {
            for (Pouch p : plugin.getPouches()) {
                if (p.getItemStack().isSimilar(player.getInventory().getItemInMainHand())) {
                    event.setCancelled(true);

                    if (p.getEconomyType() instanceof InvalidEconomyType
                            && plugin.getConfig().getBoolean("error-handling" +
                            ".prevent-opening-invalid-pouches", true)) {
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

                    if (player.getInventory().getItemInMainHand().getAmount() == 1) {
                        player.getInventory().setItemInMainHand(null);
                    } else {
                        player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                        player.updateInventory();
                    }

                    super.usePouch(player, p);
                }
            }
        } else {
            for (Pouch p : super.plugin.getPouches()) {
                if (p.getItemStack().isSimilar(player.getInventory().getItemInOffHand())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
