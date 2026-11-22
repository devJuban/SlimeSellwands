package com.artillexstudios.axsellwands.hooks.StorageHook;

import com.artillexstudios.axsellwands.AxSellwands;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.*;
import com.artillexstudios.axsellwands.utils.slimefun.BlockMenu;
import com.artillexstudios.axsellwands.utils.slimefun.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axsellwands.AxSellwands.*;

public class StorageIntegrationManager {

    public enum StorageType {
        UNIT,
        BARREL,
        GENERIC,
        NONE;

        public static StorageType from(String s){
            if (s == null) return NONE;

            if (s.equalsIgnoreCase("UNIT")) return UNIT;
            if (s.equalsIgnoreCase("BARREL")) return BARREL;

            if (s.equalsIgnoreCase("GENERIC")) return GENERIC;

            return NONE;
        }
    }

    private static final List<StorageHook> storageHooks = new ArrayList<>();

    public static void setup() {
        storageHooks.clear();
        AxSellwands.refreshPluginInfo();

        if (IE) storageHooks.add(new InfinityExpansion());
        if (FM) storageHooks.add(new FluffyMachines());
        if (GR) storageHooks.add(new GenericSlimefunStorage());
    }

    public static boolean isStorage(Block block) {

        for (StorageHook hook : getStorageHook()) {
            if (hook.isStorage(block)) return true;
        }

        return false;
    }

    public static StorageType getType(Block block) {
        for (StorageHook hook : getStorageHook()) {
            StorageType type = hook.getType(block);

            if (type != StorageType.NONE) {
                return type;
            }
        }

        return StorageType.NONE;
    }

    public static void emptyStorage(Block block) {
        for (StorageHook hook : getStorageHook()) {
            hook.emptyStorage(block);
        }
    }

    @Nullable
    public static ItemStack getContents(Block block) {
        for (StorageHook hook : getStorageHook()) {
            ItemStack result = hook.getContents(block);
            if (result != null) return result;
        }

        return null;
    }

    @Nullable
    public static BlockMenu getBlockMenu(Block block) {
        return BlockStorage.getInventory(block);
    }

    @Nullable
    public static BlockMenu getBlockMenu(Location location) {
        return BlockStorage.getInventory(location);
    }

    public static Object getSlimefunItem(Block block) {
        return BlockStorage.check(block);
    }

    public static List<StorageHook> getStorageHook() {
        return storageHooks;
    }

//    public static String getLocationInfo(org.bukkit.Location location, String key) {
//        Object value = callBlockStorage("getLocationInfo", location, key);
//        return value == null ? null : String.valueOf(value);
//    }
//
//    public static void addBlockInfo(org.bukkit.Location location, String key, String value) {
//        callBlockStorage("addBlockInfo", location, key, value);
//    }
//
//    private static Object callBlockStorage(String methodName, Object... args) {
//        if (Bukkit.getPluginManager().getPlugin("Slimefun") == null) return null;
//        try {
//            Class<?> storage = Class.forName("me.mrCookieSlime.Slimefun.api.BlockStorage");
//            for (Method method : storage.getMethods()) {
//                if (!method.getName().equals(methodName) || method.getParameterTypes().length != args.length) continue;
//                boolean matches = true;
//                Class<?>[] types = method.getParameterTypes();
//                for (int i = 0; i < args.length; i++) {
//                    if (args[i] == null) { if (types[i].isPrimitive()) matches = false; }
//                    else if (!wrap(types[i]).isAssignableFrom(args[i].getClass())) matches = false;
//                }
//                if (matches) return method.invoke(null, args);
//            }
//        } catch (Throwable ignored) { }
//        return null;
//    }
//
//    private static Class<?> wrap(Class<?> type) {
//        if (!type.isPrimitive()) return type;
//        if (type == int.class) return Integer.class;
//        if (type == long.class) return Long.class;
//        if (type == boolean.class) return Boolean.class;
//        if (type == double.class) return Double.class;
//        if (type == float.class) return Float.class;
//        if (type == short.class) return Short.class;
//        if (type == byte.class) return Byte.class;
//        if (type == char.class) return Character.class;
//        return type;
//    }
}
