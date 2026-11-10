package com.artillexstudios.axsellwands.hooks.StorageHook;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface StorageHook {

    boolean isStorage(Block b);
    ItemStack getContents(Block b);
    void emptyStorage(Block b);
}
