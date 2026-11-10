package com.artillexstudios.axintegrations.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
/**
 * called every time when the integration manager is reloaded
 * during this event it is possible to register new integrations
 */
public class AxIntegrationsReloadEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public AxIntegrationsReloadEvent() {
        super(!Bukkit.isPrimaryThread());
    }

        @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
