//package com.leonardobishop.moneypouch.economytype;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//public class TokenManagerEconomyType extends EconomyType {
//
//    private TokenManager tokenManager;
//
//    public TokenManagerEconomyType(String prefix, String suffix) {
//        super(prefix, suffix);
//        tokenManager = (TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
//    }
//
//    @Override
//    public void processPayment(Player player, long amount) {
//        if (!player.isOnline()) {
//            throw new AssertionError("Player is offline!");
//        }
//        player.giveExp(Integer.parseInt(String.valueOf(amount)));
//    }
//
//    @Override
//    public String toString() {
//        return "XP";
//    }
//
//}
