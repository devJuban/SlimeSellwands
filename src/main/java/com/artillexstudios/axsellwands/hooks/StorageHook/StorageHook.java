package com.artillexstudios.axsellwands.hooks.StorageHook;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface StorageHook {

    boolean isStorage(Block b);
    @Nullable
    ItemStack getContents(Block b);
    void emptyStorage(Block b);
}
