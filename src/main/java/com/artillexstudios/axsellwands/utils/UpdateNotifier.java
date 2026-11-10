package com.artillexstudios.axsellwands.utils;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axsellwands.AxSellwands;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class UpdateNotifier implements Listener {
    private static Config config;
    private static Config lang;
    private static boolean onJoin;
    private static String prefix;
    private static String updateNotifier;

    private final String current;
    private String latest = null;
    private boolean newest = true;

    public static void init(Config config, Config lang) {
        UpdateNotifier.config = config;
        UpdateNotifier.lang = lang;
        reload();
    }

    public static void reload() {
        onJoin = config.getBoolean("update-notifier.on-join", true);
        prefix = config.getString("prefix");
        updateNotifier = lang.getString("update-notifier");
    }

    public UpdateNotifier() {
        this.current = AxSellwands.getInstance().getDescription().getVersion();

        AxSellwands.getInstance().getServer().getPluginManager().registerEvents(this, AxSellwands.getInstance());

        long time = 30L * 60L * 20L;
        Scheduler.get().runAsyncTimer(t -> {
            this.latest = readVersion();
            this.newest = !isOutdated(current);

            if (latest == null || newest) return;
            Scheduler.get().runLaterAsync(t2 -> {
                Bukkit.getConsoleSender().sendMessage(getMessage());
            }, 50L);
            t.cancel();
        }, 0, time);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (latest == null || newest) return;
        if (!onJoin) return;
        if (!event.getPlayer().hasPermission(AxSellwands.getInstance().getName().toLowerCase() + ".update-notify")) return;
        Scheduler.get().runLaterAsync(t -> {
            event.getPlayer().sendMessage(getMessage());
        }, 50L);
    }

    private String getMessage() {
        HashMap<String, String> map = new HashMap<>();
        map.put("%current%", current);
        map.put("%latest%", latest);
        return StringUtils.formatToString(String.format("%s %s", prefix, updateNotifier), map);
    }

    @Nullable
    private String readVersion() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(String.format("https://www.artillex-studios.com/api/v1/resource/%s/latest-version", AxSellwands.getInstance().getName()));
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200) return null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.readLine();
            }
        } catch (Exception ex) {
            return null;
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public String getLatest() {
        return latest;
    }

    public boolean isOutdated(String current) {
        if (latest == null) return false;
        String[] parts1 = latest.split("\\.");
        String[] parts2 = current.split("\\.");
        for (int i = 0; i < 3; i++) {
            int num1 = Integer.parseInt(parts1[i]);
            int num2 = Integer.parseInt(parts2[i]);
            if (num1 > num2) {
                return true;
            } else if (num1 < num2) {
                return false;
            }
        }
        return false;
    }
}
