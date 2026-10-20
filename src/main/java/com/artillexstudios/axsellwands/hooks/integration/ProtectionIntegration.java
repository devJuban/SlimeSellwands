package com.artillexstudios.axsellwands.hooks.integration;

import com.artillexstudios.axsellwands.AxSellwands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ProtectionIntegration {
    public enum Permission { BREAK }
    private ProtectionIntegration() { }

    /**
     * WorldGuard is the protection provider available in the 1.8.8-compatible
     * dependency surface. Keep its API reflective so this plugin can load when
     * WorldGuard is absent and on both its legacy and current API lines.
     */
    public static boolean hasPermission(Player player, Location location, Permission permission) {
        if (player == null || location == null) return false;
        if (AxSellwands.HOOKS != null
                && !AxSellwands.HOOKS.getBoolean("hooks.protection-plugins.WorldGuard", true)) return true;

        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null || !worldGuard.isEnabled()) return true;

        try {
            // WorldGuard 6 (used with Minecraft 1.8.8).
            Object wgPlugin = staticCall(worldGuard.getClass(), "inst");
            Method canBuild = findMethod(wgPlugin.getClass(), "canBuild", Player.class, Block.class);
            if (canBuild != null) return Boolean.TRUE.equals(canBuild.invoke(wgPlugin, player, location.getBlock()));

            // WorldGuard 7 (used by current Paper releases).
            ClassLoader loader = worldGuard.getClass().getClassLoader();
            Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard", true, loader);
            Object instance = staticCall(wgClass, "getInstance");
            Object platform = call(instance, "getPlatform");
            Object localPlayer = call(wgPlugin, "wrapPlayer", player);
            Object worldEditWorld = adapt(loader, player.getWorld());
            Object sessionManager = call(platform, "getSessionManager");
            if (Boolean.TRUE.equals(call(sessionManager, "hasBypass", localPlayer, worldEditWorld))) return true;

            Object regionContainer = call(platform, "getRegionContainer");
            Object query = call(regionContainer, "createQuery");
            Object worldEditLocation = adapt(loader, location);
            Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags", true, loader);
            if (testState(loader, query, worldEditLocation, localPlayer, flagsClass, "BLOCK_BREAK")) return true;
            return testState(loader, query, worldEditLocation, localPlayer, flagsClass, "BUILD");
        } catch (Throwable error) {
            // If a detected protection plugin cannot be queried, fail closed.
            Bukkit.getLogger().warning("[SlimeSellwands] WorldGuard is present but its permission check failed; sellwand use is denied. " + error.getClass().getSimpleName());
            return false;
        }
    }

    private static boolean testState(ClassLoader loader, Object query, Object location, Object player,
                                     Class<?> flagsClass, String flagName) throws Exception {
        Field flagField = flagsClass.getField(flagName);
        Object flag = flagField.get(null);
        Method test = findCompatibleMethod(query.getClass(), "testState", location, player, flag);
        return test != null && Boolean.TRUE.equals(test.invoke(query, location, player, flag));
    }

    private static Object adapt(ClassLoader loader, Object value) throws Exception {
        Class<?> adapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter", true, loader);
        for (Method method : adapter.getMethods()) {
            if (method.getName().equals("adapt") && java.lang.reflect.Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isInstance(value)) {
                return method.invoke(null, value);
            }
        }
        throw new NoSuchMethodException("BukkitAdapter.adapt(" + value.getClass().getName() + ")");
    }

    private static Object staticCall(Class<?> type, String name) throws Exception {
        return type.getMethod(name).invoke(null);
    }

    private static Object call(Object target, String name, Object... arguments) throws Exception {
        Method method = findCompatibleMethod(target.getClass(), name, arguments);
        if (method == null) throw new NoSuchMethodException(target.getClass().getName() + "." + name);
        return method.invoke(target, arguments);
    }

    private static Method findMethod(Class<?> type, String name, Class<?>... parameters) {
        try { return type.getMethod(name, parameters); }
        catch (NoSuchMethodException ignored) { return null; }
    }

    private static Method findCompatibleMethod(Class<?> type, String name, Object... arguments) {
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean match = true;
            for (int i = 0; i < parameters.length; i++) {
                if (arguments[i] != null && !wrap(parameters[i]).isAssignableFrom(arguments[i].getClass())) {
                    match = false;
                    break;
                }
            }
            if (match) return method;
        }
        return null;
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        return type;
    }
}
