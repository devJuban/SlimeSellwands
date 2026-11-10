package com.artillexstudios.axapi.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class Title {
    private final String title;
    private final String subtitle;
    private final int fadeIn, stay, fadeOut;
    private Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title; this.subtitle = subtitle; this.fadeIn = fadeIn; this.stay = stay; this.fadeOut = fadeOut;
    }
    public static Title create(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        return new Title(title, subtitle, fadeIn, stay, fadeOut);
    }
    public void send(Player player) {
        try {
            Method method = player.getClass().getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            method.invoke(player, title, subtitle, fadeIn, stay, fadeOut);
            return;
        } catch (Throwable ignored) { }
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            String nms = handle.getClass().getPackage().getName();
            Class<?> component = Class.forName(nms + ".IChatBaseComponent");
            Class<?> serializer = Class.forName(nms + ".IChatBaseComponent$ChatSerializer");
            Class<?> action = Class.forName(nms + ".PacketPlayOutTitle$EnumTitleAction");
            Class<?> packet = Class.forName(nms + ".PacketPlayOutTitle");
            Method send = handle.getClass().getField("playerConnection").getType().getMethod("sendPacket", Class.forName(nms + ".Packet"));
            Object connection = handle.getClass().getField("playerConnection").get(handle);
            Object times = packet.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
            send.invoke(connection, times);
            sendTitlePart(handle, connection, nms, component, serializer, action, packet, send, "TITLE", title);
            sendTitlePart(handle, connection, nms, component, serializer, action, packet, send, "SUBTITLE", subtitle);
        } catch (Throwable ignored) { }
    }
    private static void sendTitlePart(Object handle, Object connection, String nms, Class<?> component,
                                      Class<?> serializer, Class<?> action, Class<?> packet, Method send,
                                      String part, String text) throws Exception {
        Object titleAction = Enum.valueOf((Class) action, part);
        Object chat = serializer.getMethod("a", String.class).invoke(null, "{\"text\":\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}");
        send.invoke(connection, packet.getConstructor(action, component).newInstance(titleAction, chat));
    }
}
