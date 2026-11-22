package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Optional Lands protection adapter without a Java 17 LandsAPI dependency. */
public class LandsProtection extends ProtectionIntegration {
    private static final String API_CLASS = "me.angeschossen.lands.api.LandsIntegration";
    private static final String FLAGS_CLASS = "me.angeschossen.lands.api.flags.type.Flags";
    private Class<?> apiType;
    private Class<?> flagsType;
    private Object api;

    public LandsProtection() {
        super("Lands");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Lands");
        if (plugin == null || !plugin.isEnabled()) return false;
        ClassLoader loader = plugin.getClass().getClassLoader();
        try {
            apiType = Class.forName(API_CLASS, false, loader);
            flagsType = Class.forName(FLAGS_CLASS, false, loader);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            Method of = findMethod(apiType, "of", IntegrationManager.getPlugin());
            if (of == null) throw new NoSuchMethodException(API_CLASS + ".of(JavaPlugin)");
            api = of.invoke(null, IntegrationManager.getPlugin());
            return api != null;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not initialize Lands integration", error);
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return hasPermission(player, location, "BLOCK_PLACE");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return hasPermission(player, location, "BLOCK_BREAK");
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return hasPermission(player, location, "INTERACT_GENERAL");
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return hasPermission(player, location, "INTERACT_CONTAINER");
    }

    private boolean hasPermission(Player player, Location location, String flagName) {
        if (player == null || api == null) return true;
        try {
            Object landWorld = call(api, "getWorld", location.getWorld());
            if (landWorld == null) return true;
            Field field = flagsType.getField(flagName);
            Object flag = field.get(null);
            return Boolean.TRUE.equals(call(landWorld, "hasRoleFlag", player.getUniqueId(), location, flag));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Lands permission check failed", error);
        }
    }

    private static Object call(Object target, String name, Object... arguments) throws ReflectiveOperationException {
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
                if (arguments[index] != null && !parameters[index].isAssignableFrom(arguments[index].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) return method;
        }
        return null;
    }
}
