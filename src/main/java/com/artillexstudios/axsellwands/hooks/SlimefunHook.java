package com.artillexstudios.axsellwands.hooks;

import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnitAPI;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;

import static com.artillexstudios.axsellwands.AxSellwands.BIE;
import static com.artillexstudios.axsellwands.AxSellwands.IE;
import static com.artillexstudios.axsellwands.AxSellwands.FM;
import static com.artillexstudios.axsellwands.AxSellwands.SF;

/**
 * @deprecated Use {@link com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager}
 */
@Deprecated
public class SlimefunHook {
    static int[] OUTPUT_SLOTS = {24,25};
    static int IE_OUTPUT_SLOT = 16;

    // Fluffy Machines API

    public static boolean isBarrel(Block b){
        if (FM.getKey()){
            return BlockStorage.check(b) instanceof Barrel;
        } else return false;
    }

    public static Barrel getBarrel(Block b){
        return (Barrel) BlockStorage.check(b);
    }

    public static ItemStack getBarrelContents(Block b){
        Barrel barrel = getBarrel(b);
        int amount = barrel.getStored(b);
        ItemStack stored = barrel.getStoredItem(b);
        if (stored == null) return null;
        BlockMenu menu = BlockStorage.getInventory(b);

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir() || item.getType() != stored.getType()) continue;
            amount += item.getAmount();
        }

        if (amount > 0){
            return new ItemStack(stored.getType(), amount);
        } else {
            return null;
        }
    }

    public static void emptyBarrel(Block b){
        Barrel barrel = getBarrel(b);
        if (barrel == null) return;
        BlockMenu menu = BlockStorage.getInventory(b);
        barrel.setStored(b, 0);

        for (int slot : OUTPUT_SLOTS){
            if (menu.getItemInSlot(slot) == null || menu.getItemInSlot(slot).getType().isAir()) continue;
            menu.replaceExistingItem(slot, null);
        }
    }

    // InfinityExpansion API

    public static boolean isUnit(Block b){
        if (BIE.getKey()){
            boolean isSign = getUnitBySign(b) != null;
            return StorageUnitAPI.isUnit(b) || isSign;
        } else if (IE.getKey()){
            boolean isSign = getUnitBySign(b) != null;
            return BlockStorage.check(b) instanceof StorageUnit || isSign;
        } else return false;
    }

    public static StorageUnit getUnit(Block b){
        if (BIE.getKey()){
            return StorageUnitAPI.getUnit(b);
        } else if (IE.getKey()){
            return (StorageUnit) BlockStorage.check(b);
        } else return null;
    }

    public static ItemStack getContents(Block b){
        if (BIE.getKey()){
            return StorageUnitAPI.getContents(b);
        } else if (IE.getKey()){
            BlockMenu menu = BlockStorage.getInventory(b);
            int stored = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "stored")) ;
            ItemStack item = menu.getItemInSlot(13);
            if (item == null || item.getType().isAir()) return null;
            stored += menu.getItemInSlot(IE_OUTPUT_SLOT).getAmount();
            return new ItemStack(item.getType(), stored);
        } else return null;
    }

    public static void emptyUnit(Block b){
        if (BIE.getKey()){
            StorageUnitAPI.emptyUnit(b);
        } else if (IE.getKey()){
            BlockMenu menu = BlockStorage.getInventory(b);
            menu.replaceExistingItem(IE_OUTPUT_SLOT, null);
            BlockStorage.addBlockInfo(b, "stored", "0");
        }
    }

    public static AbstractMap.SimpleEntry<StorageUnit, Location> getUnitBySign(Block b){
        if (BIE.getKey()){
            return StorageUnitAPI.getUnitBySign(b);
        } else if (IE.getKey()){
            if (BlockStorage.check(b) instanceof StorageUnit){
                return new AbstractMap.SimpleEntry<>(getUnit(b), b.getLocation());
            } else {
                if (b.getBlockData() instanceof WallSign wallSign){
                    Block unitBlock = b.getRelative(wallSign.getFacing().getOppositeFace());
                    return new AbstractMap.SimpleEntry<>(getUnit(unitBlock), unitBlock.getLocation());
                } else if (b.getState() instanceof Sign){
                    Block unitBlock = b.getRelative(0, -1, 0);
                    if (BlockStorage.check(unitBlock) instanceof StorageUnit){
                        return new AbstractMap.SimpleEntry<>(getUnit(unitBlock), unitBlock.getLocation());
                    }
                }
                return null;
            }
        } else return null;
    }

    // Slimefun API

    public static boolean isSlimefunItem(Block b){
        if (SF.getKey()){
            return BlockStorage.check(b) != null;
        } else return false;
    }
}
