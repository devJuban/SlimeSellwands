package com.artillexstudios.axapi.utils;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class ContainerUtils {
    public static final ContainerUtils INSTANCE = new ContainerUtils();
    private ContainerUtils() { }
    public void addOrDrop(Inventory inventory, List<ItemStack> items, Location location) {
        for (ItemStack item : items) {
            Map<Integer, ItemStack> remaining = inventory.addItem(item);
            for (ItemStack stack : remaining.values()) location.getWorld().dropItemNaturally(location, stack);
        }
    }
}
