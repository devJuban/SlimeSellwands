package com.artillexstudios.axsellwands.hooks.StorageHook;

import com.artillexstudios.axsellwands.hooks.StorageHook.api.BetterInfinityExpansion;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.FluffyMachines;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.InfinityExpansion;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axsellwands.AxSellwands.*;

/** Optional Slimefun storage support kept behind reflection so the base plugin also loads without Slimefun. */
public class StorageIntegrationManager {
    private static final List<StorageHook> storageHooks = new ArrayList<>();

    public static void setup() {
        storageHooks.clear();
        if (BIE.getKey()) storageHooks.add(new BetterInfinityExpansion());
        if (IE.getKey()) storageHooks.add(new InfinityExpansion());
        if (FM.getKey()) storageHooks.add(new FluffyMachines());
    }

    public static boolean isStorage(Block block) {
        for (StorageHook hook : storageHooks) if (hook.isStorage(block)) return true;
        return false;
    }
    public static void emptyStorage(Block block) { for (StorageHook hook : storageHooks) hook.emptyStorage(block); }
    @Nullable public static ItemStack getContents(Block block) {
        for (StorageHook hook : storageHooks) {
            ItemStack result = hook.getContents(block);
            if (result != null) return result;
        }
        return null;
    }
    @Nullable public static BlockMenuAccess getBlockMenu(Block block) {
        Object menu = callBlockStorage("getInventory", block);
        return menu == null ? null : new BlockMenuAccess(menu);
    }
    public static List<StorageHook> getStorageHook() { return storageHooks; }
    public static boolean isSlimefunItem(Block block) {
        return SF.getKey() && CONFIG.getBoolean("slimefun-integration", true) && getSlimefunItem(block) != null;
    }
    public static Object getSlimefunItem(Block block) { return callBlockStorage("check", block); }
    public static String getLocationInfo(org.bukkit.Location location, String key) {
        Object value = callBlockStorage("getLocationInfo", location, key);
        return value == null ? null : String.valueOf(value);
    }
    public static void addBlockInfo(org.bukkit.Location location, String key, String value) {
        callBlockStorage("addBlockInfo", location, key, value);
    }

    private static Object callBlockStorage(String methodName, Object... args) {
        if (Bukkit.getPluginManager().getPlugin("Slimefun") == null) return null;
        try {
            Class<?> storage = Class.forName("me.mrCookieSlime.Slimefun.api.BlockStorage");
            for (Method method : storage.getMethods()) {
                if (!method.getName().equals(methodName) || method.getParameterTypes().length != args.length) continue;
                boolean matches = true;
                Class<?>[] types = method.getParameterTypes();
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) { if (types[i].isPrimitive()) matches = false; }
                    else if (!wrap(types[i]).isAssignableFrom(args[i].getClass())) matches = false;
                }
                if (matches) return method.invoke(null, args);
            }
        } catch (Throwable ignored) { }
        return null;
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
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

    public static final class BlockMenuAccess {
        private final Object menu;
        private BlockMenuAccess(Object menu) { this.menu = menu; }
        public boolean hasViewer() { return Boolean.TRUE.equals(invoke("hasViewer")); }
        public ItemStack getItemInSlot(int slot) { return (ItemStack) invoke("getItemInSlot", slot); }
        public void replaceExistingItem(int slot, ItemStack item) { invoke("replaceExistingItem", slot, item); }
        private Object invoke(String name, Object... args) {
            try {
                for (Method method : menu.getClass().getMethods()) {
                    if (method.getName().equals(name) && method.getParameterTypes().length == args.length) return method.invoke(menu, args);
                }
            } catch (Throwable ignored) { }
            return null;
        }
    }
}
