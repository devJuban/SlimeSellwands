package com.artillexstudios.axapi.utils;

import org.bukkit.Bukkit;

public final class PaperUtils {
    private PaperUtils() { }
    public static boolean isFolia() {
        try { Class.forName("io.papermc.paper.threadedregions.RegionizedServer"); return true; }
        catch (ClassNotFoundException ignored) { return false; }
    }
}
