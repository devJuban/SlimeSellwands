package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageHook;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap.SimpleEntry;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getBlockMenu;

public class InfinityExpansion implements StorageHook {

    public final int[] OUTPUT_SLOTS = {16};

    @Override
    public boolean isStorage(Block b) {
        return BlockStorage.check(b) instanceof StorageUnit || getStorageUnit(b) != null;
    }

    @Override
    @Nullable
    public ItemStack getContents(Block b) {
        SimpleEntry<StorageUnit, Location> unit = getStorageUnit(b);
        if (unit == null) return null;
        BlockMenu menu = getBlockMenu(b);
        if (menu == null) return null;
        ItemStack stored = menu.getItemInSlot(13);
        if (stored == null) return null;
        int amount = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "stored"));

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir() || item.getType() != stored.getType()) continue;
            amount += item.getAmount();
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    public void emptyStorage(Block b) {
        if (getStorageUnit(b) == null) return;
        BlockMenu menu = getBlockMenu(b);
        if (menu == null) return;

        for (int slot : OUTPUT_SLOTS){
            menu.replaceExistingItem(slot, null);
        }

        BlockStorage.addBlockInfo(b, "stored", "0");
    }

    @Nullable
    private SimpleEntry<StorageUnit, Location> getStorageUnit(Block b){
        if (BlockStorage.check(b) instanceof StorageUnit){
            return new SimpleEntry<>(getUnit(b), b.getLocation());
        } else {
            if (b.getBlockData() instanceof WallSign wallSign){
                Block unitBlock = b.getRelative(wallSign.getFacing().getOppositeFace());
                if (BlockStorage.check(unitBlock) instanceof StorageUnit){
                    return new SimpleEntry<>(getUnit(unitBlock), unitBlock.getLocation());
                }
            } else if (b.getState() instanceof Sign){
                Block unitBlock = b.getRelative(0, -1, 0);
                if (BlockStorage.check(unitBlock) instanceof StorageUnit){
                    return new SimpleEntry<>(getUnit(unitBlock), unitBlock.getLocation());
                }
            }
            return null;
        }
    }

    private StorageUnit getUnit(Block b){
        return (StorageUnit) BlockStorage.check(b);
    }
}
