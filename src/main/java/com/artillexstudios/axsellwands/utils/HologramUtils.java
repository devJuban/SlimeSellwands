package com.artillexstudios.axsellwands.utils;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axsellwands.AxSellwands.CONFIG;
import static com.artillexstudios.axsellwands.AxSellwands.LANG;

/** Bukkit-only hologram fallback that works on legacy servers without display entities. */
public final class HologramUtils {
    private HologramUtils() { }

    public static void spawnHologram(@NotNull Player player, @NotNull Location location, HashMap<String, String> replacements) {
        Vector diff = location.toVector().subtract(player.getLocation().toVector());
        diff.subtract(diff.clone().normalize());
        Location spawn = player.getLocation().toVector().add(diff).toLocation(location.getWorld());
        List<String> lines = new ArrayList<>();
        for (String line : LANG.getStringList("sell-hologram")) {
            for (Map.Entry<String, String> entry : replacements.entrySet()) line = line.replace(entry.getKey(), entry.getValue());
            lines.add(StringUtils.formatToString(line));
        }

        final List<Entity> stands = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            Location lineLocation = spawn.clone().add(0, (lines.size() - i - 1) * 0.25, 0);
            ArmorStand stand = spawn.getWorld().spawn(lineLocation, ArmorStand.class);
            stand.setVisible(false);
            stand.setCustomName(lines.get(i));
            stand.setCustomNameVisible(true);
            stand.setGravity(false);
            try { stand.setMarker(true); } catch (NoSuchMethodError ignored) { }
            try { stand.setSmall(true); } catch (NoSuchMethodError ignored) { }
            try { stand.setBasePlate(false); } catch (NoSuchMethodError ignored) { }
            stands.add(stand);
        }
        Scheduler.get().runLaterAt(spawn, task -> {
            for (Entity stand : stands) if (stand.isValid()) stand.remove();
        }, CONFIG.getInt("hologram.stay-time-ticks", 30));
    }
}
