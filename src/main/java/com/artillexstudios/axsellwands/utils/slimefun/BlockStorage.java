package com.artillexstudios.axsellwands.utils.slimefun;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class BlockStorage {

    private static String BlockStorageClass = "me.mrCookieSlime.Slimefun.api.BlockStorage";

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
        if (Bukkit.getPluginManager().getPlugin("slimefun") == null) return null;
        
        try {
            Class<?> storage = Class.forName(BlockStorageClass);

            for (Method name : storage.getDeclaredMethods()) {
                if (name.getName().equals(method) &&
                        name.getParameterTypes().length == args.length
                ) {
                    return name.invoke(storage, args);
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }
}
