package com.artillexstudios.axsellwands;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
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
import java.util.AbstractMap.SimpleEntry;

public final class AxSellwands extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static Config HOOKS;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;

    public static SimpleEntry<Boolean, String> BIE = getPluginInfo(getPlugin("BetterInfinityExpansion"));
    public static SimpleEntry<Boolean, String> IE = getPluginInfo(getPlugin("InfinityExpansion"));
    public static SimpleEntry<Boolean, String> FM = getPluginInfo(getPlugin("FluffyMachines"));
    public static SimpleEntry<Boolean, String> SF = getPluginInfo(getPlugin("Slimefun"));

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

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"));
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"));
        HOOKS = new Config(new File(getDataFolder(), "hooks.yml"), getResource("hooks.yml"));

        MESSAGEUTILS = new MessageUtils(LANG, CONFIG);

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

        if (CONFIG.getBoolean("slimefun-integration", false) && SF.getKey()) {

           if (BIE.getKey() && compare(BIE.getValue(), "1.2.3")){
               sendMessage("&aBetterInfinityExpansion detected, integration enabled.");
           } else if (BIE.getKey() && !compare(BIE.getValue(), "1.2.3")){
               sendMessage("&2BetterInfinityExpansion detected but is lower than v1.2.3, using InfinityExpansion methods.");
               sendMessage("&2You can safely ignore this message, but is always recommended to use V1.2.3 or higher.");

               BIE = new SimpleEntry<>(false, BIE.getValue());
               IE = new SimpleEntry<>(true, IE.getValue());
           } else if (IE.getKey()) {
               sendMessage("&aInfinityExpansion detected, integration enabled.");
           }

           if (FM.getKey()){
               sendMessage("&aFluffyMachines detected, integration enabled.");
           }

           if (!BIE.getKey() && !IE.getKey() && !FM.getKey()){
                sendMessage("&4No supported plugins detected.");
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

    public static Plugin getPlugin(String plugin){
        return Bukkit.getPluginManager().getPlugin(plugin);
    }

    public static SimpleEntry<Boolean, String> getPluginInfo(Plugin p){
        return new SimpleEntry<>(
                p != null,// && p.isEnabled(),
                p == null ? "" : p.getDescription().getVersion()
        );
    }

    public static boolean compare(String v1, String v2){
        String[] left = v1.split("\\.");
        String[] right = v2.split("\\.");
        for (int i = 0; i < Math.max(left.length, right.length); i++) {
            int a = i < left.length ? Integer.parseInt(left[i]) : 0;
            int b = i < right.length ? Integer.parseInt(right[i]) : 0;
            if (a != b) return a > b;
        }
        return true;
    }

    public void sendMessage(String msg){
        String prefix = "&a[" + this.getName() + "] ";
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString(prefix + msg));
    }

}
