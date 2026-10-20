package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/** Optional IridiumSkyblock protection adapter without a hard API dependency. */
public class IridiumSkyblockProtection extends ProtectionIntegration {
    private static final String API_CLASS = "com.iridium.iridiumskyblock.api.IridiumSkyblockAPI";
    private static final String PERMISSION_CLASS = "com.iridium.iridiumskyblock.dependencies.iridiumteams.PermissionType";
    private Class<?> apiType;
    private Class<?> permissionType;
    private Object api;

    public IridiumSkyblockProtection() {
        super("IridiumSkyblock");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("IridiumSkyblock");
        if (plugin == null || !plugin.isEnabled()) return false;
        ClassLoader providerLoader = plugin.getClass().getClassLoader();
        try {
            apiType = Class.forName(API_CLASS, false, providerLoader);
            permissionType = Class.forName(PERMISSION_CLASS, false, providerLoader);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            Method getInstance = apiType.getMethod("getInstance");
            api = getInstance.invoke(null);
            return api != null;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not initialize IridiumSkyblock API", error);
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
        return hasPermission(player, location, "INTERACT");
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return hasPermission(player, location, "OPEN_CONTAINERS");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean hasPermission(Player player, Location location, String permissionName) {
        if (player == null || api == null) return true;
        try {
            Object islandOptional = invoke(api, "getIslandViaLocation", location);
            Object island = invoke(islandOptional, "orElse", (Object) null);
            if (island == null) return true;
            Object user = invoke(api, "getUser", player);
            if (user == null) return true;
            Object permission = Enum.valueOf((Class<? extends Enum>) permissionType.asSubclass(Enum.class), permissionName);
            return Boolean.TRUE.equals(invoke(api, "getIslandPermission", island, user, permission));
        } catch (ReflectiveOperationException | IllegalArgumentException error) {
            throw new IllegalStateException("IridiumSkyblock permission check failed", error);
        }
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
