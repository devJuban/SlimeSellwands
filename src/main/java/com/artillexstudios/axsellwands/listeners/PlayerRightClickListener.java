package com.artillexstudios.axsellwands.listeners;

import com.artillexstudios.axapi.items.NBTWrapper;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axsellwands.sellwands.Sellwand;
import com.artillexstudios.axsellwands.sellwands.Sellwands;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.*;

// try to merge this into SellwandUseListener if possible :) (prob not tho)
public class PlayerRightClickListener implements Listener {

    @EventHandler
    public void onPlayerRightClickEvent(PlayerRightClickEvent rightClickEvent) {
        PlayerInteractEvent interactEvent = rightClickEvent.getInteractEvent();

        Optional<SlimefunItem> optional = rightClickEvent.getSlimefunBlock();
        if (optional.isEmpty()) return;

        ItemStack item = interactEvent.getItem();
        if (item == null) return;
        Block block = interactEvent.getClickedBlock();
        if (block == null) return;

        if (!isStorage(block)) return;
        WrappedItemStack wrapped = WrappedItemStack.wrap(item);
        NBTWrapper wrapper = new NBTWrapper(wrapped);
        String type = wrapper.getString("axsellwands-type");
        if (type == null) return;
        Sellwand sellwand = Sellwands.getSellwands().get(type);
        if (sellwand == null) return;

        rightClickEvent.cancel();
    }
}
