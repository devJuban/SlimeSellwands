package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.types.CustomBlockIntegration;
import com.artillexstudios.axintegrations.utils.IntegrationScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Optional ItemsAdder custom block bridge with no provider API dependency. */
public class ItemsAdderCustomBlock extends CustomBlockIntegration {
    private static final String API_CLASS = "dev.lone.itemsadder.api.CustomBlock";
    private Class<?> customBlockType;

    public ItemsAdderCustomBlock() {
        super("ItemsAdder");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemsAdder");
        if (plugin == null || !plugin.isEnabled()) return false;
        try {
            customBlockType = Class.forName(API_CLASS, false, plugin.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isCustomBlock(String itemId) {
        try {
            return staticCall("getInstance", itemId) != null;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Override
    public boolean isCustomBlock(Location location) {
        try {
            return staticCall("byAlreadyPlaced", location.getBlock()) != null;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Override
    public boolean place(final String itemId, final Location location) {
        final Object customBlock;
        try {
            customBlock = staticCall("getInstance", itemId);
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
        if (customBlock == null) return false;

        final Plugin plugin = IntegrationManager.getPlugin();
        IntegrationScheduler.runLater(plugin, new Runnable() {
            @Override
            public void run() {
                location.getBlock().setType(Material.NOTE_BLOCK);
                IntegrationScheduler.runLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            call(customBlock, "place", itemId, location);
                        } catch (ReflectiveOperationException error) {
                            throw new IllegalStateException("Could not place ItemsAdder block " + itemId, error);
                        }
                    }
                }, 1L);
            }
        }, 0L);
        return true;
    }

    @Override
    public boolean remove(Location location) {
        try {
            Object customBlock = staticCall("byAlreadyPlaced", location.getBlock());
            if (customBlock == null) return false;
            call(customBlock, "remove");
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private Object staticCall(String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(customBlockType, name, arguments);
        if (method == null || !Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException(customBlockType.getName() + "." + name);
        }
        return method.invoke(null, arguments);
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
