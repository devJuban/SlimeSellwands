package com.artillexstudios.axsellwands.hooks.StorageHook;

import com.artillexstudios.axsellwands.hooks.StorageHook.api.BetterInfinityExpansion;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.FluffyMachines;
import com.artillexstudios.axsellwands.hooks.StorageHook.api.InfinityExpansion;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.artillexstudios.axsellwands.AxSellwands.SF;

public class StorageIntegrationManager {

    private static List<StorageAPI> StorageHook = new ArrayList<>();

    public static void setup(){
        StorageHook.clear();

        if (checkPlugin("BetterInfinityExpansion")){
            StorageHook.add(new BetterInfinityExpansion());
        }
        if (checkPlugin("InfinityExpansion")){
            StorageHook.add(new InfinityExpansion());
        }
        if (checkPlugin("FluffyMachines")){
            StorageHook.add(new FluffyMachines());
        }
    }

    public static boolean isStorage(Block b){
        for (StorageAPI api : getStorageHook()){
            if (api.isStorage(b)) return true;
        }

        return false;
    }

    public static void emptyStorage(Block b){
        for (StorageAPI api : getStorageHook()){
            api.emptyStorage(b);
        }
    }

    @Nullable
    public static ItemStack getContents(Block b){
        for (StorageAPI api : getStorageHook()){
            if (api.getContents(b) != null){
                return api.getContents(b);
            }
        }

        return null;
    }

    @Nullable
    public static BlockMenu getBlockMenu(Block b){
        for (StorageAPI api : getStorageHook()){
            if (api.getContents(b) != null){
                return api.getBlockMenu(b);
            }
        }

        return null;
    }

    public static List<StorageAPI> getStorageHook(){
        return StorageHook;
    }

    public static boolean isSlimefunItem(Block b){
        if (SF.getKey()){
            return BlockStorage.check(b) != null;
        } else return false;
    }

    private static boolean checkPlugin(String plugin){
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }
}
