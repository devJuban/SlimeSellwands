package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageAPI;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FluffyMachines implements StorageAPI {

    public final int[] OUTPUT_SLOTS = {24,25};

    @Override
    public boolean isStorage(Block b) {
        return BlockStorage.check(b) instanceof Barrel;
    }

    @Override
    @Nullable
    public ItemStack getContents(Block b) {
        Barrel barrel = getBarrel(b);
        if (barrel == null) return null;
        ItemStack stored = barrel.getStoredItem(b);
        if (stored == null) return null;
        BlockMenu menu = getBlockMenu(b);
        if (menu == null) return null;
        int amount = barrel.getStored(b);

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir() || item.getType() != stored.getType()) continue;
            amount += item.getAmount();
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    @Nullable
    public BlockMenu getBlockMenu(Block b) {
        return BlockStorage.getInventory(b);
    }

    @Override
    public void emptyStorage(Block b) {
        BlockMenu menu = getBlockMenu(b);
        if (menu == null) return;
        Barrel barrel = getBarrel(b);
        if (barrel == null) return;
        barrel.setStored(b, 0);

        for (int slot : OUTPUT_SLOTS){
            menu.replaceExistingItem(slot, null);
        }

        barrel.updateHologram(b, null, "&cEmpty");
    }

    @Nullable
    private Barrel getBarrel(Block b){
        if (!isStorage(b)) return null;
        return (Barrel) BlockStorage.check(b);
    }
}
