package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Optional PlotSquared protection adapter without Java 21 provider APIs. */
public class PlotSquaredProtection extends ProtectionIntegration {
    private static final String API_CLASS = "com.plotsquared.core.PlotSquared";
    private static final String LOCATION_CLASS = "com.plotsquared.core.location.Location";
    private Class<?> apiType;
    private Class<?> locationType;
    private Object manager;

    public PlotSquaredProtection() {
        super("PlotSquared");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        if (plugin == null || !plugin.isEnabled()) return false;
        ClassLoader loader = plugin.getClass().getClassLoader();
        try {
            apiType = Class.forName(API_CLASS, false, loader);
            locationType = Class.forName(LOCATION_CLASS, false, loader);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            Object api = invokeStatic(apiType, "get");
            manager = invoke(api, "getPlotAreaManager");
            return manager != null;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not initialize PlotSquared", error);
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return hasPermission(player, location);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return hasPermission(player, location);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return hasPermission(player, location);
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return hasPermission(player, location);
    }

    private boolean hasPermission(Player player, Location location) {
        if (player == null || manager == null) return true;
        try {
            Object plotLocation = invokeStatic(locationType, "at", location.getWorld().getName(),
                    location.getBlockX(), location.getBlockY(), location.getBlockZ());
            Object plotArea = invoke(manager, "getPlotArea", plotLocation);
            if (plotArea == null) return true;
            Object plot = invoke(plotArea, "getPlotAbs", plotLocation);
            if (plot == null) return false;
            Object uuid = player.getUniqueId();
            return Boolean.TRUE.equals(invoke(plot, "isAdded", uuid))
                    || Boolean.TRUE.equals(invoke(plot, "isOwner", uuid));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("PlotSquared permission check failed", error);
        }
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
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == boolean.class) return Boolean.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        return type;
    }
}
