package com.artillexstudios.axsellwands.hooks.integration;

import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ContainerIntegration {
    private final InventoryHolder holder;
    private ContainerIntegration(InventoryHolder holder) { this.holder = holder; }
    public static ContainerIntegration getContainerIntegration(Block block) {
        if (block == null || !(block.getState() instanceof InventoryHolder)) return null;
        return new ContainerIntegration((InventoryHolder) block.getState());
    }
    public List<ItemStack> getContents(Block block) {
        ItemStack[] items = getInventory().getContents();
        return new ArrayList<>(Arrays.asList(items));
    }
    public Inventory getInventory() { return holder.getInventory(); }
}
