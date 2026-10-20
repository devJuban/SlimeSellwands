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

/** Optional Nexo custom block bridge without a Java 21 compile dependency. */
public class NexoCustomBlock extends CustomBlockIntegration {
    private static final String BLOCKS_CLASS = "com.nexomc.nexo.api.NexoBlocks";
    private static final String ITEMS_CLASS = "com.nexomc.nexo.api.NexoItems";
    private static final String DROP_CLASS = "com.nexomc.nexo.utils.drops.Drop";
    private ClassLoader providerLoader;
    private Class<?> blocksType;
    private Class<?> itemsType;
    private Class<?> dropType;

    public NexoCustomBlock() {
        super("Nexo");
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Nexo");
        if (plugin == null || !plugin.isEnabled()) return false;
        providerLoader = plugin.getClass().getClassLoader();
        try {
            blocksType = Class.forName(BLOCKS_CLASS, false, providerLoader);
            itemsType = Class.forName(ITEMS_CLASS, false, providerLoader);
            dropType = Class.forName(DROP_CLASS, false, providerLoader);
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
            return Boolean.TRUE.equals(staticCall(blocksType, "isCustomBlock", itemId));
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Override
    public boolean isCustomBlock(Location location) {
        try {
            return Boolean.TRUE.equals(staticCall(blocksType, "isCustomBlock", location.getBlock()));
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    @Override
    public boolean place(final String itemId, final Location location) {
        final boolean note;
        final boolean string;
        try {
            note = Boolean.TRUE.equals(staticCall(itemsType, "hasMechanic", itemId, "noteblock"));
            string = Boolean.TRUE.equals(staticCall(itemsType, "hasMechanic", itemId, "stringblock"));
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
        if (!note && !string) return false;

        final Plugin plugin = IntegrationManager.getPlugin();
        IntegrationScheduler.runLater(plugin, new Runnable() {
            @Override
            public void run() {
                location.getBlock().setType(note ? Material.NOTE_BLOCK : Material.TRIPWIRE);
                IntegrationScheduler.runLater(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            staticCall(blocksType, "place", itemId, location);
                        } catch (ReflectiveOperationException error) {
                            throw new IllegalStateException("Could not place Nexo block " + itemId, error);
                        }
                    }
                }, 1L);
            }
        }, 0L);
        return true;
    }

    @Override
    public boolean remove(Location location) {
        if (!isCustomBlock(location)) return false;
        try {
            Object drop = staticCall(dropType, "emptyDrop");
            staticCall(blocksType, "remove", location, null, drop);
            return true;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private Object staticCall(Class<?> owner, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(owner, name, arguments);
        if (method == null || !Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException(owner.getName() + "." + name);
        }
        return method.invoke(null, arguments);
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
