package com.artillexstudios.axsellwands.listeners;

import com.artillexstudios.axapi.items.NBTWrapper;
import com.artillexstudios.axsellwands.AxSellwands;
import com.artillexstudios.axsellwands.sellwands.Sellwand;
import com.artillexstudios.axsellwands.sellwands.Sellwands;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;


import java.lang.reflect.Method;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.*;

public class PlayerRightClickListener implements Listener {

    private static final String slimefun4 = "io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent";
    private static final String slimefun5 = "io.github.thebusybiscuit.slimefun5.api.events.PlayerRightClickEvent";

    @SuppressWarnings("unchecked")
    public static void registerEvent() {
        PlayerRightClickListener clickListener = new PlayerRightClickListener();

        EventPriority priority = EventPriority.NORMAL;

        Class<? extends Event> eventClass  = null;
        try {
            // try Slimefun4 API first
            eventClass = (Class<? extends Event>) Class.forName(slimefun4);
        } catch (Exception e) {
            // try Slimefun5 API
            try {
                eventClass = (Class<? extends Event>) Class.forName(slimefun5);
            } catch (Exception ignored) {}
        }

        if (eventClass == null){
            AxSellwands.sendMessage("&4Failed to register PlayerRightClickListener!");
            return;
        }

        // not sure how this works, but it does!
        if (!Event.class.isAssignableFrom(eventClass)) {
            AxSellwands.sendMessage("&4PlayerRightClickEvent is not an event!");
            return;
        }

        Class<? extends Event> finalEventClass = eventClass;
        EventExecutor eventExecutor = (listener, event) -> {
            if (listener instanceof PlayerRightClickListener && event.getClass().equals(finalEventClass)) {
                clickListener.click(event);
            }
        };

        Bukkit.getPluginManager().registerEvent(
                eventClass,
                clickListener,
                priority,
                eventExecutor,
                AxSellwands.getInstance()
        );
    }


    public void click(Event rightClickEvent) {
        PlayerInteractEvent interactEvent = (PlayerInteractEvent) invoke(rightClickEvent, "getInteractEvent");
        if (interactEvent == null) return;

        ItemStack item = interactEvent.getItem();
        if (item == null) return;
        Block block = interactEvent.getClickedBlock();
        if (block == null) return;

        if (!isStorage(block)) return;
        NBTWrapper wrapper = new NBTWrapper(item);
        String type = wrapper.getString("axsellwands-type");
        if (type == null) return;
        Sellwand sellwand = Sellwands.getSellwands().get(type);
        if (sellwand == null) return;

        invoke(rightClickEvent, "cancel");
    }

    private Object invoke(Event event, String method, Object... args) {
        try {
            Class<?> classEvent = event.getClass();

            for (Method name : classEvent.getDeclaredMethods()) {
                if (name.getName().equals(method) &&
                        name.getParameterTypes().length == args.length
                ) {
                    return name.invoke(event, args);
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }
}
