package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageHook;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/** Optional BetterInfinityExpansion adapter loaded only when that plugin is installed. */
public class BetterInfinityExpansion implements StorageHook {
    private static final String API = "io.github.mooy1.infinityexpansion.items.storage.StorageUnitAPI";

    @Override public boolean isStorage(Block block) {
        return Boolean.TRUE.equals(call("isUnit", block)) || unitBySign(block) != null;
    }
    @Override public ItemStack getContents(Block block) {
        Object pair = unitBySign(block);
        Object location = value(pair, "getValue");
        return location instanceof Location ? (ItemStack) call("getContents", location) : null;
    }
    @Override public void emptyStorage(Block block) {
        Object pair = unitBySign(block);
        Object location = value(pair, "getValue");
        if (location instanceof Location) call("emptyUnit", location);
    }
    @Nullable private Object unitBySign(Block block) { return call("getUnitBySign", block); }

    private Object call(String name, Object... args) {
        try {
            Class<?> api = Class.forName(API);
            for (Method method : api.getMethods()) {
                if (!method.getName().equals(name) || method.getParameterTypes().length != args.length) continue;
                boolean matches = true;
                for (int i = 0; i < args.length; i++) if (args[i] != null && !method.getParameterTypes()[i].isAssignableFrom(args[i].getClass())) matches = false;
                if (matches) return method.invoke(null, args);
            }
        } catch (Throwable ignored) { }
        return null;
    }
    private Object value(Object target, String method) {
        if (target == null) return null;
        try { return target.getClass().getMethod(method).invoke(target); }
        catch (ReflectiveOperationException ignored) { return null; }
    }
}
