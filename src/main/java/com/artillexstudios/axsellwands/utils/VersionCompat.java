package com.artillexstudios.axsellwands.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/** Small reflective shims for Bukkit APIs added after Minecraft 1.8. */
public final class VersionCompat {
    private VersionCompat() { }

    public static void playSound(Player player, String configuredName) {
        if (configuredName == null || configuredName.isEmpty()) return;
        String name = configuredName;
        try {
            player.playSound(player.getLocation(), Sound.valueOf(name), 1.0f, 1.0f);
        } catch (IllegalArgumentException ignored) {
            String legacy = legacySoundName(name);
            if (legacy == null) return;
            try {
                player.playSound(player.getLocation(), Sound.valueOf(legacy), 1.0f, 1.0f);
            } catch (IllegalArgumentException ignoredAgain) {
                // The configured sound is unavailable on this server version.
            }
        }
    }

    public static void spawnParticle(Player player, String configuredName, Location location,
                                     int count, double offsetX, double offsetY, double offsetZ) {
        try {
            Class<?> particleClass = Class.forName("org.bukkit.Particle");
            Object particle = Enum.valueOf((Class) particleClass, configuredName);
            for (Method method : Player.class.getMethods()) {
                if (!method.getName().equals("spawnParticle") || method.getParameterTypes().length != 6) continue;
                if (method.getParameterTypes()[0] != particleClass) continue;
                method.invoke(player, particle, location, count, offsetX, offsetY, offsetZ);
                return;
            }
        } catch (Exception ignored) {
            // Bukkit did not expose particles before 1.9, or the configured particle is unsupported.
        }
    }

    private static String legacySoundName(String name) {
        if ("ENTITY_VILLAGER_YES".equals(name)) return "VILLAGER_YES";
        if ("ENTITY_VILLAGER_NO".equals(name)) return "VILLAGER_NO";
        if ("ENTITY_PLAYER_LEVELUP".equals(name)) return "LEVEL_UP";
        if ("ENTITY_EXPERIENCE_ORB_PICKUP".equals(name)) return "ORB_PICKUP";
        return null;
    }
}
