package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.events.IntegrationEvents;
import com.artillexstudios.axintegrations.events.impl.PlayerVanishChangeEvent;
import com.artillexstudios.axintegrations.types.VanishIntegration;
import de.myzelyam.api.vanish.PlayerVanishStateChangeEvent;
import de.myzelyam.api.vanish.VanishAPI;
import de.myzelyam.supervanish.SuperVanish;
import de.myzelyam.supervanish.VanishPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
public class SuperVanishVanish extends VanishIntegration implements Listener {
    private SuperVanish instance;

    public SuperVanishVanish() {
        super("SuperVanish");
    }

    @Override
    public boolean canLoad() {
        if (Bukkit.getPluginManager().getPlugin(name) == null) return false;
        return ClassUtils.INSTANCE.classExists("de.myzelyam.api.vanish.VanishAPI");
    }

    @Override
    public boolean setup() {
        instance = (SuperVanish) Bukkit.getPluginManager().getPlugin(name);
        if (instance == null) return false;
        registerListener();
        return true;
    }

    public void disable() {
        unregisterListener();
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChange(PlayerVanishStateChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getUUID());
        if (player == null) return;
        IntegrationEvents.callEvent(new PlayerVanishChangeEvent(this, player, event.isVanishing()));
    }

    @Override
    public boolean isVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }

    @Override
    public boolean canVanish(Player player) {
        return player.hasPermission("");
    }

    @Override
    public void showPlayer(Player player) {
        VanishAPI.showPlayer(player);
    }

    @Override
    public void hidePlayer(Player player) {
        VanishAPI.hidePlayer(player);
    }

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        return VanishAPI.canSee(viewer, viewed);
    }

    @Override
    public boolean canPickup(Player player) {
        VanishPlayer vanishPlayer = getVanishPlayer(player);
        if (vanishPlayer == null) return true;
        return vanishPlayer.hasItemPickUpsEnabled();
    }

    @Override
    public int getVanishPriority(Player player) {
        VanishPlayer vanishPlayer = getVanishPlayer(player);
        if (vanishPlayer == null) return 0;
        return vanishPlayer.getUsePermissionLevel();
    }

    @Override
    public int getViewPriority(Player player) {
        VanishPlayer vanishPlayer = getVanishPlayer(player);
        if (vanishPlayer == null) return 0;
        return vanishPlayer.getSeePermissionLevel();
    }

    private VanishPlayer getVanishPlayer(Player player) {
        return instance.getVanishPlayer(player);
    }
}
