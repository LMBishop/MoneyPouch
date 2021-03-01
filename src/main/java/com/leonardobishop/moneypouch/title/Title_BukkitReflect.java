package com.leonardobishop.moneypouch.title;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Title_BukkitReflect implements Title {

	private boolean loggedError = false;
	private final Title_BukkitNoTimings redundancy;

	private Map<String, Class<?>> classCache;
	private Object enumTitle;
	private Object enumSubTitle;
	private Method aMethod;
	private Constructor<?> titleConstructor;
	private Constructor<?> lengthConstructor;

	public Title_BukkitReflect() {
		this.redundancy = new Title_BukkitNoTimings();

		try {
			this.classCache = new HashMap<>();
			this.enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
			this.enumSubTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
			this.aMethod = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class);
			this.titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
			this.lengthConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(int.class, int.class, int.class);
		} catch (Exception e) {
			loggedError = true;
			Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().severe("Could not enable titles" +
					" with timings! Now using fallback, this message will not be replayed.");
		}
	}

	@Override
	public void sendTitle(Player player, String message, String submessage) {
		constructPacket(player, message, submessage);
	}

	private void constructPacket(Player player, String title, String subtitle) {
		try {
			if (loggedError) {
				redundancy.sendTitle(player, title, subtitle);
				return;
			}
			Object chatComponent = aMethod.invoke(null, "{\"text\":\""+title+"\"}");
			Object subtitleChatComponent = aMethod.invoke(null, "{\"text\":\""+subtitle+"\"}");
			Object titlePacket = titleConstructor.newInstance(enumTitle, chatComponent);
			Object lengthPacket = lengthConstructor.newInstance(0, 50, 20);
			Object subtitlePacket = titleConstructor.newInstance(enumSubTitle, subtitleChatComponent);
			sendPacket(player, title, subtitle, subtitlePacket, titlePacket, lengthPacket);
		} catch (Exception ex) {
			ex.printStackTrace();
			if (!loggedError) {
				loggedError = true;
				Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().severe("Could not send title" +
						" with timings! Now using fallback, this message will not be replayed.");
			}
			redundancy.sendTitle(player, title, subtitle);
		}
	}

	private void sendPacket(Player player, String title, String subtitle, Object... packets) {
		try {
			if (loggedError) {
				redundancy.sendTitle(player, title, subtitle);
				return;
			}
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			Method sendPacketMethod = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
			for (Object packet : packets) {
				sendPacketMethod.invoke(playerConnection, packet);
			}
		} catch (Exception ignored) {
			if (!loggedError) {
				loggedError = true;
				Bukkit.getPluginManager().getPlugin("MoneyPouch").getLogger().severe("Could not send title" +
						" with timings! Now using fallback, this message will not be replayed.");
			}
			redundancy.sendTitle(player, title, subtitle);
		}
	}

	private Class<?> getNMSClass(String name) {
		return classCache.computeIfAbsent(name, key -> {
			try {
				return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + key);
			} catch (ClassNotFoundException ignored) { }
			return null;
		});
	}

}