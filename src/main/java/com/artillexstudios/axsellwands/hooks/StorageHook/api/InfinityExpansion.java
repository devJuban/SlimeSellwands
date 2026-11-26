package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.utils.slimefun.BlockMenu;
import com.artillexstudios.axsellwands.utils.slimefun.BlockStorage;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.material.Sign;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.*;

public class InfinityExpansion implements StorageHook {

    public final int[] OUTPUT_SLOTS = {16};

    @Override
    public boolean isStorage(Block b) {
        return getStorageUnit(b) != null;
    }

    @Override
    public ItemStack getContents(Block block) {
        Location location = getStorageUnit(block);
        if (location == null) return null;
        BlockMenu menu = getBlockMenu(location);
        if (menu == null) return null;
        ItemStack stored = menu.getItemInSlot(13);
        if (stored == null) return null;
        int amount = Integer.parseInt(BlockStorage.getLocationInfo(location, "stored"));

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (isSimilar(item, stored)) {
                amount += item.getAmount();
            }
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    public StorageType getType(Block block) {
        return isStorage(block) ? StorageType.UNIT : StorageType.NONE;
    }

    @Override
    public void emptyStorage(Block block) {
        Location location = getStorageUnit(block);
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
            throw new Exception();
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

    // requires sign fixing
    @Nullable
    private Location getStorageUnit(Block block){
        if (isStorageUnit(block)){
            return block.getLocation();
        } else {
            if (isWallSign(block)) {
                Sign sign = (Sign) block.getState().getData();

                Block unitBlock = block.getRelative(sign.getFacing().getOppositeFace());
                if (isStorageUnit(unitBlock)){
                    return unitBlock.getLocation();
                }
            } else {
                Block unitBlock = block.getRelative(0, -1, 0);

                if (isStorageUnit(unitBlock)) {
                    return unitBlock.getLocation();
                }
            }

            return null;
        }
    }

    private boolean isStorageUnit(Block block) {
        Object slimefunItem = getSlimefunItem(block);
        if (slimefunItem == null) return false;
        return slimefunItem.getClass().getSimpleName().equals("StorageUnit");
    }

    private boolean isWallSign(Block block) {

        try {
            Class<?> wallSignClass = Class.forName("org.bukkit.block.data.type.WallSign");

            //if (block.getBlockData().getClass().isInstance(wallSignClass)) {
            if (wallSignClass.isAssignableFrom(block.getBlockData().getClass())) {
                return true;
            }

        } catch (ClassNotFoundException ignored) {
            Sign sign = (Sign) block.getState().getData();

            if (sign.isWallSign()) return true;
        }

        return false;
    }
}
