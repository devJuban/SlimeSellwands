package com.artillexstudios.axsellwands.hooks.StorageHook;

import com.artillexstudios.axsellwands.hooks.StorageHook.api.FluffyMachines;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.InfinityExpansion;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.StorageHook;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axsellwands.AxSellwands.*;

public class StorageIntegrationManager {

    public enum StorageType {
        UNIT,
        BARREL,
        NONE;

        public static StorageType from(String s){
            if (s == null) return NONE;

            if (s.equalsIgnoreCase("UNIT")) return UNIT;
            if (s.equalsIgnoreCase("BARREL")) return BARREL;

            return NONE;
        }
    }

    private static final List<StorageHook> StorageHook = new ArrayList<>();

    public static void setup(){
        StorageHook.clear();

        IE =  isPluginEnabled("InfinityExpansion");
        FM =  isPluginEnabled("FluffyMachines");

        if (IE)  StorageHook.add(new InfinityExpansion());
        if (FM)  StorageHook.add(new FluffyMachines());
    }

    public static boolean isStorage(Block b){

        for (StorageHook api : getStorageHook()){
            if (api.isStorage(b)) return true;
        }

        return false;
    }

    public static void emptyStorage(Block b){
        if (!isStorage(b)) return;

        for (StorageHook api : getStorageHook()){
            api.emptyStorage(b);
            return;
        }
    }

    @Nullable
    public static ItemStack getContents(Block b){
        for (StorageHook api : getStorageHook()){
            if (api.getContents(b) != null){
                return api.getContents(b);
            }
        }

        return null;
    }

    public static StorageType getType(Block b){
        if (!isStorage(b)) return StorageType.NONE;

        for (StorageHook api : getStorageHook()){
            if (api.isStorage(b)) return api.getType(b);
        }

        return StorageType.NONE;
    }

    @Nullable
    public static BlockMenu getBlockMenu(Block b){
        return BlockStorage.getInventory(b);
    }

    public static List<StorageHook> getStorageHook(){
        return StorageHook;
    }
}
