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
import com.artillexstudios.axsellwands.commands.CommandManager;
import com.artillexstudios.axsellwands.hooks.HookManager;
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

//    private static ThreadedQueue<Runnable> threadedQueue;

//    public static ThreadedQueue<Runnable> getThreadedQueue() {
//        return threadedQueue;
//    }

    public static SimpleEntry<Boolean, String> BIE = getPluginInfo(getPlugin("BetterInfinityExpansion"));
    public static SimpleEntry<Boolean, String> IE = getPluginInfo(getPlugin("InfinityExpansion"));
    public static SimpleEntry<Boolean, String> FM = getPluginInfo(getPlugin("FluffyMachines"));
    public static SimpleEntry<Boolean, String> SF = getPluginInfo(getPlugin("Slimefun"));

    public static AxPlugin getInstance() {
        return instance;
    }

    public void enable() {
        instance = this;

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        HOOKS = new Config(new File(getDataFolder(), "hooks.yml"), getResource("hooks.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

//        threadedQueue = new ThreadedQueue<>("AxSellwands-Datastore-thread");

        HookManager.setup();
        NumberUtils.reload();
        CommandManager.load();

        if (FileUtils.PLUGIN_DIRECTORY.resolve("sellwands/").toFile().mkdirs()) {
            FileUtils.copyFromResource("sellwands");
        }

        Sellwands.reload();
        getServer().getPluginManager().registerEvents(new SellwandUseListener(), this);
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
        }
    }

    public void disable() {
        //
    }

    public void updateFlags() {
        FeatureFlags.USE_LEGACY_HEX_FORMATTER.set(false);
        FeatureFlags.PACKET_ENTITY_TRACKER_ENABLED.set(true);
        FeatureFlags.HOLOGRAM_UPDATE_TICKS.set(20L);
        FeatureFlags.PACKET_ENTITY_TRACKER_THREADS.set(1);
    }

    public static Plugin getPlugin(String plugin){
        return Bukkit.getPluginManager().getPlugin(plugin);
    }

    public static SimpleEntry<Boolean, String> getPluginInfo(Plugin p){
        return new SimpleEntry<>(
                p != null,
                p == null ? "" : p.getDescription().getVersion()
        );
    }

    public static boolean compare(String v1, String v2){
        return Runtime.Version.parse(v1).compareTo(Runtime.Version.parse(v2)) >= 0;
    }

    public void sendMessage(String msg){
        String prefix = "&a[" + this.getName() + "] ";
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString(prefix + msg));
    }

}