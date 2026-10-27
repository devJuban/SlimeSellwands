package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.StorageType;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface StorageHook {

    boolean isStorage(Block b);
    @Nullable
    ItemStack getContents(Block b);
    StorageType getType(Block b);
    void emptyStorage(Block b);
}
