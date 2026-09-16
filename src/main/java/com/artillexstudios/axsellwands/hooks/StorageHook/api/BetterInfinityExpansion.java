package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageAPI;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnitAPI;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap.SimpleEntry;

public class BetterInfinityExpansion implements StorageAPI {

    public final int[] OUTPUT_SLOTS = {16};

    @Override
    public boolean isStorage(Block b) {
        return StorageUnitAPI.isUnit(b) || StorageUnitAPI.getUnitBySign(b) != null;
    }

    @Override
    @Nullable
    public ItemStack getContents(Block b) {
        SimpleEntry<StorageUnit, Location> data = getUnit(b);
        if (data == null) return null;
        return StorageUnitAPI.getContents(data.getValue());
    }

    @Override
    @Nullable
    public BlockMenu getBlockMenu(Block b) {
        return BlockStorage.getInventory(b);
    }

    @Override
    public void emptyStorage(Block b) {
        SimpleEntry<StorageUnit, Location> data = getUnit(b);
        if (data == null) return;
        StorageUnitAPI.emptyUnit(data.getValue());
    }

    @Nullable
    private SimpleEntry<StorageUnit, Location> getUnit(Block b){
        if (!isStorage(b)) return null;
        return StorageUnitAPI.getUnitBySign(b);
    }
}
