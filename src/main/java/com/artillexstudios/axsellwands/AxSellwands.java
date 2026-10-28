package com.artillexstudios.axsellwands;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.featureflags.FeatureFlags;
import com.artillexstudios.axapi.utils.logging.LoggerNameFormat;
import com.artillexstudios.axsellwands.commands.CommandManager;
import com.artillexstudios.axsellwands.hooks.HookManager;
import com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager;
import com.artillexstudios.axsellwands.listeners.CraftListener;
import com.artillexstudios.axsellwands.listeners.InventoryClickListener;
import com.artillexstudios.axsellwands.listeners.SellwandUseListener;
import com.artillexstudios.axsellwands.sellwands.Sellwands;
import com.artillexstudios.axsellwands.utils.FileUtils;
import com.artillexstudios.axsellwands.utils.NumberUtils;
import com.artillexstudios.axsellwands.utils.UpdateNotifier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;

public final class AxSellwands extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static Config HOOKS;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;

    public static boolean IE;
    public static boolean FM;

    public static AxPlugin getInstance() {
        return instance;
    }

    @Override
    public void load() {
        // remove legacy libs
        File libs = new File(getDataFolder(), "libs");
        if (libs.exists()) {
            com.artillexstudios.axapi.utils.file.FileUtils.deleteNested(libs.toPath());
        }
        File lib = new File(getDataFolder(), "lib");
        if (lib.exists()) {
            com.artillexstudios.axapi.utils.file.FileUtils.deleteNested(lib.toPath());
        }
    }

    @Override
    public void enable() {
        instance = this;

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        HOOKS = new Config(new File(getDataFolder(), "hooks.yml"), getResource("hooks.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        HookManager.setup();
        StorageIntegrationManager.setup();
        NumberUtils.reload();
        CommandManager.load();

        if (FileUtils.PLUGIN_DIRECTORY.resolve("sellwands/").toFile().mkdirs()) {
            FileUtils.copyFromResource("sellwands");
        }

        Sellwands.reload();
        SellwandUseListener.registerEvent();
        getServer().getPluginManager().registerEvents(new CraftListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

        sendMessage("&#FF5500Loaded plugin!");

        UpdateNotifier.init(CONFIG, LANG);
        if (CONFIG.getBoolean("update-notifier.enabled", true)) new UpdateNotifier();

        if (isPluginEnabled("slimefun")){

            if (isPluginEnabled("BetterInfinityExpansion")) {
                sendMessage("&aBetterInfinityExpansion detected, integration enabled.");
            } else if (IE) {
                sendMessage("&aInfinityExpansion detected, integration enabled.");
            }

            if (FM) {
                sendMessage("&aFluffyMachines detected, integration enabled.");
            }

            if (!isPluginEnabled("BetterInfinityExpansion") && !IE && !FM){
                sendMessage("&4No supported plugins detected or enabled.");
                sendMessage("&4Supported Plugins:");
                sendMessage("&4 - BetterInfinityExpansion");
                sendMessage("&4 - InfinityExpansion");
                sendMessage("&4 - FluffyMachines");
            }

        } else {
            sendMessage("&4Slimefun not found or disabled!");
        }
    }

    @Override
    public void disable() {
        //
    }

    public void updateFlags() {
        FeatureFlags.PACKET_ENTITY_TRACKER_ENABLED.set(true);
        FeatureFlags.HOLOGRAM_UPDATE_TICKS.set(20L);
        FeatureFlags.PACKET_ENTITY_TRACKER_THREADS.set(1);
        FeatureFlags.LOGGER_NAME_FORMAT.set(LoggerNameFormat.NAMEABLE);
    }


    public static boolean isPluginEnabled(String pluginName){
        Plugin plugin =  Bukkit.getPluginManager().getPlugin(pluginName);
        return  plugin != null     &&
                plugin.isEnabled() &&
                CONFIG.getSection("slimefun").getBoolean(pluginName, true);
    }

    public void sendMessage(String msg){
        String prefix = "&a[" + this.getName() + "] ";
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString(prefix + msg));
    }

}