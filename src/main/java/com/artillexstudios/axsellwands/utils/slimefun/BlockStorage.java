package com.artillexstudios.axsellwands.utils.slimefun;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class BlockStorage {

    private static final String BlockStorageClass = "me.mrCookieSlime.Slimefun.api.BlockStorage";

    @Nullable
    public static BlockMenu getInventory(Block block) {
        Object menu = invoke("getInventory", block);
        if (menu == null) return null;
        return new BlockMenu(menu);
    }

    @Nullable
    public static BlockMenu getInventory(Location location) {
        Object menu = invoke("getInventory", location);
        if (menu == null) return null;
        return new BlockMenu(menu);
    }

    public static String getLocationInfo(Location location, String key) {
        return String.valueOf(invoke("getLocationInfo", location, key));
    }

    public static void addBlockInfo(Location location, String key, String value) {
        invoke("addBlockInfo", location, key, value);
    }

    @Nullable
    public static Object check(Block block) {
        return invoke("check", block);
    }

    @Nullable
    private static Object invoke(String method, Object... args) {
        try {
            Class<?> storage = Class.forName(BlockStorageClass);

            for (Method name : storage.getDeclaredMethods()) {
                Class<?>[] types = name.getParameterTypes();
                if (types.length != args.length) continue;
                if (!name.getName().equals(method)) continue;

                boolean exit = false;
                for (int i = 0; i < types.length; i++) {
                    if (!types[i].isInstance(args[i])) {
                        exit = true;
                        break;
                    }
                }

                if (exit) continue;

                return name.invoke(storage, args);
            }
        } catch (Throwable ignored) {}
        return null;
    }
}
