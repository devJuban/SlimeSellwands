package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.StorageType;
import io.github.mooy1.infinityexpansion.items.storage.StorageCache;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getBlockMenu;

public class InfinityExpansion implements StorageHook {

    public final int[] OUTPUT_SLOTS = {16};

    @Override
    public boolean isStorage(Block b) {
        return getStorageUnit(b) != null;
    }

    @Override
    public ItemStack getContents(Block b) {
        SimpleEntry<StorageUnit, Location> data = getStorageUnit(b);
        if (data == null) return null;
        Location location = data.getValue();
        BlockMenu menu = getBlockMenu(location);
        if (menu == null) return null;
        ItemStack stored = menu.getItemInSlot(13);
        if (stored == null) return null;
        int amount = Integer.parseInt(BlockStorage.getLocationInfo(location, "stored"));

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item != null && item.getType() == stored.getType()){
                amount += item.getAmount();
            }
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    public StorageType getType(Block b) {
        return isStorage(b) ? StorageType.UNIT : StorageType.NONE;
    }

    @Override
    public void emptyStorage(Block b) {
        SimpleEntry<StorageUnit, Location> data = getStorageUnit(b);
        if (data == null) return;
        Location location = data.getValue();

        StorageCache cache = data.getKey().getCache(location);
        if (cache == null) return;
        BlockMenu menu = getBlockMenu(location);
        if (menu == null) return;

        try {
            Method setEmptyMethod = cache.getClass().getDeclaredMethod("setEmpty");
            setEmptyMethod.setAccessible(true);

            setEmptyMethod.invoke(cache);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            BlockStorage.addBlockInfo(location, "stored", "0");
        }

        for (int slot : OUTPUT_SLOTS){
            menu.replaceExistingItem(slot, null);
        }
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
