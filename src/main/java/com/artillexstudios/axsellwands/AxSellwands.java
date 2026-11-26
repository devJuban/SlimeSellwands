package com.artillexstudios.axsellwands;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;

import com.artillexstudios.axsellwands.listeners.PlayerRightClickListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

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

public final class AxSellwands extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static Config HOOKS;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;

    public static boolean IE;
    public static boolean FM;

    public static boolean GR;

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

        if (isPluginEnabled("slimefun")) {
            PlayerRightClickListener.registerEvent();
        }

        sendMessage("&#FF5500Loaded plugin!");

        UpdateNotifier.init(CONFIG, LANG);
        if (CONFIG.getBoolean("update-notifier.enabled", true)) new UpdateNotifier();

        reportDependencies(Bukkit.getConsoleSender());
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

    public static void refreshPluginInfo() {
        IE = isPluginEnabled("InfinityExpansion") ||
            isPluginEnabled("BetterInfinityExpansion");
        FM = isPluginEnabled("FluffyMachines");
        GR = CONFIG.getBoolean("GenericSlimefunStorage", false);
    }

    // requires rewrite
    public void reportDependencies(org.bukkit.command.CommandSender sender) {
        refreshPluginInfo();
        boolean slimefunEnabled = CONFIG.getBoolean("slimefun-integration", true);

//        if (slimefunEnabled && !SF.getKey()) {
//            dependencyMessage(sender, "&cMissing required dependency: Slimefun. Slimefun integration is disabled until it is installed.");
//        } else if (!slimefunEnabled) {
//            dependencyMessage(sender, "&eSlimefun integration is disabled in config.yml.");
//        } else {
//            dependencyMessage(sender, "&aDependency found: Slimefun " + SF.getValue());
//        }

        boolean SF = isPluginEnabled("slimefun");

        if (SF) {

        } else {

        }



        String economy = HOOKS.getString("hooks.economy-plugin", "Vault");
        if ("builtin".equalsIgnoreCase(economy)) {
            dependencyMessage(sender, "&aEconomy: built-in prices selected; Vault is not required for price lookup.");
        } else if (!hasPlugin(economy)) {
            dependencyMessage(sender, "&cMissing required dependency: " + economy + " (configured economy provider).");
        } else {
            dependencyMessage(sender, "&aDependency found: " + economy);
        }

        reportOptional(sender, "PlacelhoderAPI");
        reportOptional(sender, "WorldGuard");

        reportOptional(sender, "BetterInfinityExpansion");
        reportOptional(sender, "InfinityExpansion");
        reportOptional(sender, "FluffyMachines");

        if (StorageIntegrationManager.getStorageHook().isEmpty()) {
            dependencyMessage(sender, "&eNo named storage expansion detected.");
        }
    }

    private void reportOptional(org.bukkit.command.CommandSender sender, String name) {
        dependencyMessage(sender, (hasPlugin(name) ? "&aOptional dependency found: " : "&7Optional dependency missing: ") + name);
    }

    private void dependencyMessage(org.bukkit.command.CommandSender sender, String message) {
        sender.sendMessage(StringUtils.formatToString("&a[" + getName() + "] " + message));
    }

    private static boolean hasPlugin(String name) {
        Plugin plugin = getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }

    public static boolean isPluginEnabled(String pluginName) {
        return isPluginEnabled(pluginName, true);
    }

    public static boolean isPluginEnabled(String pluginName, boolean def){
        Plugin plugin =  Bukkit.getPluginManager().getPlugin(pluginName);
        return  plugin != null     &&
                plugin.isEnabled() &&
                CONFIG.getSection("slimefun").getBoolean(pluginName, def);
    }

    public static void sendMessage(String msg){
        String prefix = "&a[" + getInstance().getName() + "] ";
        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString(prefix + msg));
    }
}