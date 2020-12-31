//TODO fix this
//package com.leonardobishop.moneypouch.title;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import java.lang.reflect.Constructor;
//
//public class Title_BukkitReflect implements Title {
//
//	private boolean loggedError = false;
//	private final Title_BukkitNoTimings redudancy;
//
//	public Title_BukkitReflect() {
//		this.redudancy = new Title_BukkitNoTimings();
//	}
//
//	private void constructPacket(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
//		try {
//			if (loggedError) {
//				redudancy.sendTitle(player, title, subtitle);
//				return;
//			}
//			Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
//			Object enumSubTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
//			Object chat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+title+"\"}");
//			Object subchat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+subtitle+"\"}");
//			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
//			Object packet = titleConstructor.newInstance(enumTitle, chat, fadeIn, stay, fadeOut);
//			Object packet2 = titleConstructor.newInstance(enumSubTitle, subchat, fadeIn, stay, fadeOut);
//			sendPacket(player, packet, title, subtitle);
//			sendPacket(player, packet2, title, subtitle);
//		} catch (Exception ignored) {
//			ignored.printStackTrace();
//			if (!loggedError) {
//				loggedError = true;
//				Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().severe("Could not send title" +
//						" with timings! Now using fallback, this message will not be replayed.");
//			}
//			redudancy.sendTitle(player, title, subtitle);
//		}
//	}
//
//	private void sendPacket(Player player, Object packet, String title, String subtitle) {
//		try {
//			if (loggedError) {
//				redudancy.sendTitle(player, title, subtitle);
//				return;
//			}
//			Object handle = player.getClass().getMethod("getHandle").invoke(player);
//			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
//			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
//		} catch (Exception ignored) {
//			if (!loggedError) {
//				loggedError = true;
//				Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().severe("Could not send title" +
//						" with timings! Now using fallback, this message will not be replayed.");
//			}
//			redudancy.sendTitle(player, title, subtitle);
//		}
//	}
//
//	private Class<?> getNMSClass(String name) {
//		try {
//			return Class.forName("net.minecraft.server" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
//		} catch (ClassNotFoundException ignored) {
////			System.out.println("bong");
//		}
//		return null;
//	}
//
//	@Override
//	public void sendTitle(Player player, String message, String submessage) {
//		constructPacket(player, message, submessage, 0, 50, 20);
//	}
//}