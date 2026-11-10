package com.artillexstudios.axapi.libs.boostedyaml.block.implementation;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Set;

/** Wrapper kept to avoid changing the plugin's existing configuration call sites. */
public final class Section {
    private final ConfigurationSection section;
    public Section(ConfigurationSection section) { this.section = section; }
    public Object get(String path) { return section.get(path); }
    public String getString(String path) { return section.getString(path); }
    public String getString(String path, String fallback) { return section.getString(path, fallback); }
    public boolean getBoolean(String path) { return section.getBoolean(path); }
    public boolean getBoolean(String path, boolean fallback) { return section.getBoolean(path, fallback); }
    public int getInt(String path) { return section.getInt(path); }
    public int getInt(String path, int fallback) { return section.getInt(path, fallback); }
    public double getDouble(String path, double fallback) { return section.getDouble(path, fallback); }
    public List<String> getStringList(String path) { return section.getStringList(path); }
    public Set<String> getKeys() { return section.getKeys(false); }
    public ConfigurationSection unwrap() { return section; }
}
