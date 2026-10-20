package com.artillexstudios.axapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class ActionBar {
    private final String message;
    private ActionBar(String message) { this.message = message; }
    public static ActionBar create(String message) { return new ActionBar(message); }
    public void send(Player player) {
        try {
            Class<?> messageType = Class.forName("net.md_5.bungee.api.ChatMessageType");
            Object actionBar = Enum.valueOf((Class) messageType, "ACTION_BAR");
            Class<?> baseComponent = Class.forName("net.md_5.bungee.api.chat.BaseComponent");
            Class<?> textComponent = Class.forName("net.md_5.bungee.api.chat.TextComponent");
            Object components = textComponent.getMethod("fromLegacyText", String.class).invoke(null, message);
            Object spigot = player.getClass().getMethod("spigot").invoke(player);
            for (Method method : spigot.getClass().getMethods()) {
                Class<?>[] p = method.getParameterTypes();
                if (method.getName().equals("sendMessage") && p.length == 2 && p[0] == messageType && p[1].isArray() && p[1].getComponentType() == baseComponent) {
                    method.invoke(spigot, actionBar, components);
                    return;
                }
            }
        } catch (Throwable ignored) { }
        sendLegacyPacket(player, message);
    }

    private void sendLegacyPacket(Player player, String message) {
        try {
            String craft = Bukkit.getServer().getClass().getPackage().getName();
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            String nms = handle.getClass().getPackage().getName();
            Class<?> component = Class.forName(nms + ".IChatBaseComponent");
            Class<?> serializer = Class.forName(nms + ".IChatBaseComponent$ChatSerializer");
            Object text = serializer.getMethod("a", String.class).invoke(null, "{\"text\":\"" + escape(message) + "\"}");
            Class<?> packet = Class.forName(nms + ".PacketPlayOutChat");
            Object packetObject = packet.getConstructor(component, byte.class).newInstance(text, (byte) 2);
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            connection.getClass().getMethod("sendPacket", Class.forName(nms + ".Packet")).invoke(connection, packetObject);
        } catch (Throwable ignored) { }
    }

    private static String escape(String text) { return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n"); }
}
