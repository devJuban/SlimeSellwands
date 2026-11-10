package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** WorldGuard 6/7 protection adapter without a compile-time WorldGuard API. */
public class WorldGuardProtection extends ProtectionIntegration {
    private static final String PLUGIN_CLASS = "com.sk89q.worldguard.bukkit.WorldGuardPlugin";
    private static final String WORLDGUARD_CLASS = "com.sk89q.worldguard.WorldGuard";
    private static final String FLAGS_CLASS = "com.sk89q.worldguard.protection.flags.Flags";
    private static final String ADAPTER_CLASS = "com.sk89q.worldedit.bukkit.BukkitAdapter";
    private Class<?> worldGuardType;
    private Class<?> pluginType;
    private Class<?> flagsType;
    private Class<?> adapterType;
    private Object plugin;
    private Object platform;

    public WorldGuardProtection() {
        super("WorldGuard");
    }

    @Override
    public boolean canLoad() {
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null || !worldGuard.isEnabled()) return false;
        ClassLoader providerLoader = worldGuard.getClass().getClassLoader();
        try {
            pluginType = Class.forName(PLUGIN_CLASS, false, providerLoader);
            try {
                worldGuardType = Class.forName(WORLDGUARD_CLASS, false, providerLoader);
                flagsType = Class.forName(FLAGS_CLASS, false, providerLoader);
                adapterType = Class.forName(ADAPTER_CLASS, false, providerLoader);
            } catch (ClassNotFoundException legacyWorldGuard6) {
                worldGuardType = null;
            }
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            plugin = invokeStatic(pluginType, "inst");
            if (plugin == null) return false;
            if (worldGuardType != null) {
                Object instance = invokeStatic(worldGuardType, "getInstance");
                platform = invoke(instance, "getPlatform");
            }
            return true;
        } catch (ReflectiveOperationException | LinkageError error) {
            throw new IllegalStateException("Could not initialize WorldGuard protection", error);
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return testLegacyOrModern(player, location, "BLOCK_PLACE", true);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return testLegacyOrModern(player, location, "BLOCK_BREAK", true);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return testLegacyOrModern(player, location, "INTERACT", false);
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return testLegacyOrModern(player, location, "CHEST_ACCESS", false);
    }

    private boolean testLegacyOrModern(Player player, Location location, String flagName, boolean allowBuildFallback) {
        if (player == null || plugin == null) return false;
        if (worldGuardType == null) {
            try {
                Method canBuild = findMethod(plugin.getClass(), "canBuild", player, location.getBlock());
                return canBuild != null && Boolean.TRUE.equals(canBuild.invoke(plugin, player, location.getBlock()));
            } catch (ReflectiveOperationException | LinkageError ignored) {
                return false;
            }
        }

        try {
            Object localPlayer = invoke(plugin, "wrapPlayer", player);
            Object world = invokeStatic(adapterType, "adapt", player.getWorld());
            Object sessions = invoke(platform, "getSessionManager");
            if (Boolean.TRUE.equals(invoke(sessions, "hasBypass", localPlayer, world))) return true;

            Object regionContainer = invoke(platform, "getRegionContainer");
            Object query = invoke(regionContainer, "createQuery");
            Object worldGuardLocation = invokeStatic(adapterType, "adapt", location);
            if (testState(query, worldGuardLocation, localPlayer, flagName)) return true;
            return allowBuildFallback && testState(query, worldGuardLocation, localPlayer, "BUILD");
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    private boolean testState(Object query, Object location, Object player, String flagName) throws ReflectiveOperationException {
        Field field = flagsType.getField(flagName);
        Object flag = field.get(null);
        Method test = findMethod(query.getClass(), "testState", location, player, flag);
        return test != null && Boolean.TRUE.equals(test.invoke(query, location, player, flag));
    }

    private static Object invokeStatic(Class<?> owner, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(owner, name, arguments);
        if (method == null || !Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException(owner.getName() + "." + name);
        }
        return method.invoke(null, arguments);
    }

    private static Object invoke(Object target, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(target.getClass(), name, arguments);
        if (method == null) throw new NoSuchMethodException(target.getClass().getName() + "." + name);
        return method.invoke(target, arguments);
    }

    private static Method findMethod(Class<?> owner, String name, Object... arguments) {
        for (Method method : owner.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean matches = true;
            for (int index = 0; index < parameters.length; index++) {
                if (arguments[index] != null && !wrap(parameters[index]).isAssignableFrom(arguments[index].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) return method;
        }
        return null;
    }

    private static Class<?> wrap(Class<?> type) {
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
