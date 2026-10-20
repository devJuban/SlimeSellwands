package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageHook;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;


import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getBlockMenu;
import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getSlimefunItem;
import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getLocationInfo;
import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.addBlockInfo;

public class InfinityExpansion implements StorageHook {

    public final int[] OUTPUT_SLOTS = {16};

    @Override
    public boolean isStorage(Block b) {
        return isStorageUnit(getSlimefunItem(b)) || getStorageUnit(b) != null;
    }

    @Override
    public ItemStack getContents(Block b) {
        Location unit = getStorageUnit(b);
        if (unit == null) return null;
        com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.BlockMenuAccess menu = getBlockMenu(b);
        if (menu == null) return null;
        ItemStack stored = menu.getItemInSlot(13);
        if (stored == null) return null;
        int amount = Integer.parseInt(getLocationInfo(unit, "stored"));

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType() == org.bukkit.Material.AIR || item.getType() != stored.getType()) continue;
            amount += item.getAmount();
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    public void emptyStorage(Block b) {
        Location unit = getStorageUnit(b);
        if (unit == null) return;
        com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.BlockMenuAccess menu = getBlockMenu(b);
        if (menu == null) return;

        for (int slot : OUTPUT_SLOTS){
            menu.replaceExistingItem(slot, null);
        }

        addBlockInfo(unit, "stored", "0");
    }

    @Nullable
    private Location getStorageUnit(Block b){
        if (isStorageUnit(getSlimefunItem(b))){
            return b.getLocation();
        } else {
            Sign sign = (Sign) b.getState().getData();
            if (sign.isWallSign()){
                Block unitBlock = b.getRelative(sign.getFacing().getOppositeFace());
                if (isStorageUnit(getSlimefunItem(unitBlock))){
                    return unitBlock.getLocation();
                }
            } else if (b.getState().getType().name().endsWith("_SIGN")
                    && !b.getState().getType().name().endsWith("_WALL_SIGN")) {
                Block unitBlock = b.getRelative(0, -1, 0);
                if (isStorageUnit(getSlimefunItem(unitBlock))){
                    return unitBlock.getLocation();
                }
            }
            return null;
        }
    }

    private boolean isStorageUnit(Object item) {
        return item != null && item.getClass().getName().endsWith(".StorageUnit");
    }
}
