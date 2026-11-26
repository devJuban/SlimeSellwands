package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.StorageType;
import com.artillexstudios.axsellwands.utils.slimefun.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.*;

public class FluffyMachines implements StorageHook {

    public final int[] OUTPUT_SLOTS = {24,25};

    @Override
    public boolean isStorage(Block b) {
        return getBarrel(b) != null;
    }

    @Override
    public ItemStack getContents(Block b) {
        Barrel barrel = getBarrel(b);
        if (barrel == null) return null;
        ItemStack stored = barrel.getStoredItem(b);
        if (stored == null) return null;
        BlockMenu menu = getBlockMenu(b);
        if (menu == null) return null;
        int amount = barrel.getStored(b);

        for (int slot : OUTPUT_SLOTS) {
            ItemStack item = menu.getItemInSlot(slot);
            if (isSimilar(item, stored)) {
                amount += item.getAmount();
            }
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    public StorageType getType(Block b) {
        return isStorage(b) ? StorageType.BARREL : StorageType.NONE;
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
        Object slimefunItem = getSlimefunItem(b);
        if (!isBarrel(slimefunItem)) return null;

        return new Barrel(getSlimefunItem(b));
    }

    private boolean isBarrel(Object slimefunItem) {
        if (slimefunItem == null) return false;
        String className = slimefunItem.getClass().getSimpleName();

        if (className.equals("Barrel")) return true;
        return className.equals("MiniBarrel");
    }

    // "reimpl" the barrel class
    public static class Barrel {

        Object barrel;

        public Barrel(Object barrel) {
            this.barrel = barrel;
        }

        public int getStored(Block block) {
            Object amount = invoke("getStored", block);
            return amount == null ? 0 : (int) amount;
        }

        public ItemStack getStoredItem(Block block) {
            return (ItemStack) invoke("getStoredItem", block);
        }

        public void setStored(Block block, int amount) {
            invoke("setStored", block, amount);
        }

        public void updateHologram(Block block, String top, String bottom) {
            invoke("updateHologram", block, top, bottom);
        }

        private Object invoke(String method, Object... args) {
            try {
                Class<?> storage = this.barrel.getClass();

                for (Method name : storage.getMethods()) {
                    if (name.getName().equals(method) &&
                            name.getParameterTypes().length == args.length
                    ) {
                        return name.invoke(barrel, args);
                    }
                }
            } catch (Throwable ignored) {}
            return null;
        }
    }
}
