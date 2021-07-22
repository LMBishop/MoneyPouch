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


public class UseListenerLatest extends UseListener implements Listener {

    public UseListenerLatest(MoneyPouch plugin) {
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
            onRightClickInMainHand(player, event);
        } else {
            for (Pouch p : super.plugin.getPouches()) {
                if (p.getItemStack().isSimilar(player.getInventory().getItemInOffHand())) {
                    event.setCancelled(true);
                }
            }
        }
    }

}
