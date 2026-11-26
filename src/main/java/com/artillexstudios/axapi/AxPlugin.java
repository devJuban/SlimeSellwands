package com.artillexstudios.axapi;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AxPlugin extends JavaPlugin {
    private static AxPlugin instance;

    public void load() { }
    public void enable() { }
    public void disable() { }

    @Override
    public final void onLoad() {
        instance = this;
        load();
    }

    @Override
    public final void onEnable() {
        instance = this;
        enable();
    }

    @Override
    public final void onDisable() {
        disable();
        if (instance == this) instance = null;
    }

    public static AxPlugin getInstance() {
        return instance;
    }
}
