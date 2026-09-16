package com.artillexstudios.axsellwands.hooks.StorageHook;

import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface StorageAPI {

    boolean isStorage(Block b);
    ItemStack getContents(Block b);
    BlockMenu getBlockMenu(Block b);
    void emptyStorage(Block b);
}
