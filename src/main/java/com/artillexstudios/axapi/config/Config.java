package com.artillexstudios.axapi.config;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/** Small Bukkit-YAML-backed replacement for the AxAPI configuration methods used by this plugin. */
public final class Config {
    private final File file;
    private final InputStream defaults;
    private YamlConfiguration document = new YamlConfiguration();

    public Config(File file) {
        this(file, null);
    }

    public Config(File file, InputStream defaults) {
        this.file = file;
        this.defaults = defaults;
        if (defaults != null) {
            try {
                if (!file.exists()) {
                    File parent = file.getParentFile();
                    if (parent != null) parent.mkdirs();
                    Files.copy(defaults, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try { defaults.close(); } catch (IOException ignored) { }
            }
        }
        reload();
    }

    public boolean reload() {
        try {
            document = new YamlConfiguration();
            if (file.exists()) document.load(file);
            return true;
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        try {
            File parent = file.getParentFile();
            if (parent != null) parent.mkdirs();
            document.save(file);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public YamlConfiguration getBackingDocument() { return document; }
    public Section getSection(String path) {
        ConfigurationSection section = document.getConfigurationSection(path);
        return section == null ? null : new Section(section);
    }
    public String getString(String path) { return document.getString(path); }
    public String getString(String path, String fallback) { return document.getString(path, fallback); }
    public boolean getBoolean(String path) { return document.getBoolean(path); }
    public boolean getBoolean(String path, boolean fallback) { return document.getBoolean(path, fallback); }
    public int getInt(String path) { return document.getInt(path); }
    public int getInt(String path, int fallback) { return document.getInt(path, fallback); }
    public long getLong(String path) { return document.getLong(path); }
    public long getLong(String path, long fallback) { return document.getLong(path, fallback); }
    public float getFloat(String path, float fallback) { return (float) document.getDouble(path, fallback); }
    public List<String> getStringList(String path) { return document.getStringList(path); }
    public void set(String path, Object value) { document.set(path, value); }
}
