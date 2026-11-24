package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.utils.slimefun.BlockMenu;
import com.artillexstudios.axsellwands.utils.slimefun.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.*;

public class InfinityExpansion implements StorageHook {

    public final int[] OUTPUT_SLOTS = {16};

    @Override
    public boolean isStorage(Block b) {
        return getStorageUnit(b) != null;
    }

    @Override
    public ItemStack getContents(Block b) {
        Location unit = getStorageUnit(b);
        if (unit == null) return null;
        BlockMenu menu = getBlockMenu(b);
        if (menu == null) return null;
        ItemStack stored = menu.getItemInSlot(13);
        if (stored == null) return null;
        int amount = Integer.parseInt(BlockStorage.getLocationInfo(unit, "stored"));

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (isSimilar(item, stored)) {
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
        Location location = getStorageUnit(b);
        if (location == null) return;

//        StorageCache cache = data.getKey().getCache(location);
//        if (cache == null) return;
        BlockMenu menu = getBlockMenu(location);
        if (menu == null) return;

        try {
//            Method setEmptyMethod = cache.getClass().getDeclaredMethod("setEmpty");
//            setEmptyMethod.setAccessible(true);
//
//            setEmptyMethod.invoke(cache);
            throw new Exception("");
        } catch (Exception exception) {
            BlockStorage.addBlockInfo(location, "stored", "0");

            // remove the output slot and then line below will remove the excess
            // this method should work the same as setEmpty?
            for (int slot : OUTPUT_SLOTS){
                menu.replaceExistingItem(slot, null);
            }
        }

        for (int slot : OUTPUT_SLOTS){
            menu.replaceExistingItem(slot, null);
        }
    }

    @Nullable
    private Location getStorageUnit(Block b){
        if (isStorageUnit(getSlimefunItem(b))){
            return b.getLocation();
        } else {
            if (b.getState().getData() instanceof Sign &&
                    ((Sign) b.getState().getData()).isWallSign()
            ) {
                Sign sign = (Sign) b.getState().getData();

                Block unitBlock = b.getRelative(sign.getFacing().getOppositeFace());
                if (isStorageUnit(getSlimefunItem(unitBlock))){
                    return unitBlock.getLocation();
                }
            } else if (b.getState() instanceof org.bukkit.block.Sign) {
                Block unitBlock = b.getRelative(0, -1, 0);

                if (isStorageUnit(getSlimefunItem(unitBlock))) {
                    return unitBlock.getLocation();
                }
            }

            return null;
        }
    }

    private boolean isStorageUnit(Object block) {
        return block != null && block.getClass().getName().endsWith(".StorageUnit");
    }
}
