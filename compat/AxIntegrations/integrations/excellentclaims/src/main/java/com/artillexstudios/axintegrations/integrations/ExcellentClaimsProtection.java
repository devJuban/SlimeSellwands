package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.Arrays;

/** Optional ExcellentClaims adapter with no compile-time provider dependency. */
public class ExcellentClaimsProtection extends ProtectionIntegration {
    private static final String API_CLASS = "su.nightexpress.excellentclaims.ClaimsAPI";
    private ClassLoader providerLoader;
    private Object manager;

    public ExcellentClaimsProtection() {
        super("ExcellentClaims");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ExcellentClaims");
        if (plugin == null || !plugin.isEnabled()) return false;
        providerLoader = plugin.getClass().getClassLoader();
        try {
            Class.forName(API_CLASS, false, providerLoader);
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        try {
            Class<?> api = Class.forName(API_CLASS, true, providerLoader);
            Method method = api.getMethod("getClaimManager");
            manager = method.invoke(null);
            return manager != null;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not get ExcellentClaims manager", error);
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return query("canBuild", player, location);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return query("canBreak", player, location);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return query("canUseBlock", player, location.getBlock(), Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return canInteract(player, location);
    }

    private boolean query(String name, Object... arguments) {
        try {
            for (Method method : manager.getClass().getMethods()) {
                if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length) continue;
                Class<?>[] parameterTypes = method.getParameterTypes();
                boolean matches = true;
                for (int index = 0; index < parameterTypes.length; index++) {
                    if (arguments[index] != null && !parameterTypes[index].isInstance(arguments[index])) {
                        matches = false;
                        break;
                    }
                }
                if (matches) return Boolean.TRUE.equals(method.invoke(manager, arguments));
            }
            throw new NoSuchMethodException(manager.getClass().getName() + "." + name + Arrays.toString(arguments));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("ExcellentClaims " + name + " check failed", error);
        }
    }
}
