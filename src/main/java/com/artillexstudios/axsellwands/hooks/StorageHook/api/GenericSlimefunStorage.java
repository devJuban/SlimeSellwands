package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageHook;
import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getSlimefunItem;

/** Reflective fallback for Slimefun storage addons without a shared API. */
public final class GenericSlimefunStorage implements StorageHook {
    private static final String[] CONTENT_METHODS = {"getStoredItem", "getContents"};
    private static final String[] EMPTY_METHODS = {"emptyUnit", "empty", "clear", "setStored"};

    @Override
    public boolean isStorage(Block block) {
        Object item = getSlimefunItem(block);
        return item != null && hasContentMethod(item.getClass());
    }

    @Override
    @Nullable
    public ItemStack getContents(Block block) {
        Object item = getSlimefunItem(block);
        if (item == null) return null;
        for (Method method : item.getClass().getMethods()) {
            for (String name : CONTENT_METHODS) {
                if (!method.getName().equals(name) || !acceptsTarget(method)) continue;
                try {
                    Object value = invoke(item, method, block);
                    if (value instanceof ItemStack) return (ItemStack) value;
                } catch (ReflectiveOperationException ignored) { }
            }
        }
        return null;
    }

    @Override
    public void emptyStorage(Block block) {
        Object item = getSlimefunItem(block);
        if (item == null) return;
        for (Method method : item.getClass().getMethods()) {
            for (String name : EMPTY_METHODS) {
                if (!method.getName().equals(name) || !acceptsTarget(method)) continue;
                try {
                    if (method.getName().equals("setStored") && method.getParameterTypes().length == 2) {
                        method.invoke(item, target(method.getParameterTypes()[0], block), 0);
                    } else if (method.getParameterTypes().length == 0) {
                        method.invoke(item);
                    } else {
                        method.invoke(item, target(method.getParameterTypes()[0], block));
                    }
                    return;
                } catch (ReflectiveOperationException ignored) { }
            }
        }
    }

    private boolean hasContentMethod(Class<?> type) {
        for (Method method : type.getMethods()) {
            for (String name : CONTENT_METHODS) {
                if (method.getName().equals(name) && acceptsTarget(method)) return true;
            }
        }
        return false;
    }

    private Object invoke(Object item, Method method, Block block) throws ReflectiveOperationException {
        return method.getParameterTypes().length == 0
                ? method.invoke(item) : method.invoke(item, target(method.getParameterTypes()[0], block));
    }

    private Object target(Class<?> type, Block block) {
        return Location.class.isAssignableFrom(type) ? block.getLocation() : block;
    }

    private boolean acceptsTarget(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        return parameters.length == 0
                || (parameters.length == 1 && (parameters[0].isAssignableFrom(Block.class)
                || parameters[0].isAssignableFrom(Location.class)))
                || (parameters.length == 2 && parameters[0].isAssignableFrom(Block.class)
                && (parameters[1] == int.class || parameters[1] == Integer.class));
    }
}