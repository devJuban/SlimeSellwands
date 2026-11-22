package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Optional SimpleClaimSystem API adapter without a hard provider dependency. */
public class SimpleClaimSystem2Protection extends ProtectionIntegration {
    private static final String API_CLASS = "fr.xyness.SimpleClaimSystem.API.SCS_API";
    private static final String PROVIDER_CLASS = "fr.xyness.SimpleClaimSystem.API.SCS_API_Provider";
    private Class<?> providerType;
    private Object api;

    public SimpleClaimSystem2Protection() {
        super("SimpleClaimSystem");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("SimpleClaimSystem");
        if (plugin == null || !plugin.isEnabled()) return false;
        try {
            ClassLoader loader = plugin.getClass().getClassLoader();
            Class.forName(API_CLASS, false, loader);
            providerType = Class.forName(PROVIDER_CLASS, false, loader);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            Object registered = invokeStatic(providerType, "isRegistered");
            if (!Boolean.TRUE.equals(registered)) return false;
            api = invokeStatic(providerType, "get");
            return api != null;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not initialize SimpleClaimSystem API", error);
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return isAllowed(player, location, "place_block");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return isAllowed(player, location, "destroy_block");
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return isAllowed(player, location, "place_block");
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return isAllowed(player, location, "interact_chest");
    }

    private boolean isAllowed(Player player, Location location, String permission) {
        if (player == null || api == null) return true;
        try {
            Object result = invoke(api, "hasPermission", location.getChunk(), player.getUniqueId(), permission);
            return Boolean.TRUE.equals(result);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("SimpleClaimSystem permission check failed", error);
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
