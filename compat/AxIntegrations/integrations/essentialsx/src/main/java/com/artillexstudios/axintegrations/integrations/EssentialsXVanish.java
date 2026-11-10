package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.VanishIntegration;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
public class EssentialsXVanish extends VanishIntegration {
    private IEssentials api;

    public EssentialsXVanish() {
        super("EssentialsX");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.earth2me.essentials.IEssentials");
    }

    @Override
    public boolean setup() {
        api = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (api == null) return false;
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }


    @Override
    public boolean isVanished(Player player) {
        User user = getUser(player);
        if (user == null) return false;
        return user.isVanished();
    }

    @Override
    public boolean canVanish(Player player) {
        return player.hasPermission("essentials.vanish");
    }

    @Override
    public void showPlayer(Player player) {
        User user = getUser(player);
        if (user == null) return;
        user.setVanished(false);
    }

    @Override
    public void hidePlayer(Player player) {
        User user = getUser(player);
        if (user == null) return;
        user.setVanished(true);
    }

    @Override
    public boolean canSee(Player viewer, Player viewed) {
        User viewedUser = getUser(viewed);
        if (viewedUser == null) return true;
        return !viewedUser.isHiddenFrom(viewer);
    }

    @Override
    public boolean canPickup(Player player) {
        return player.hasPermission("essentials.vanish.pickup");
    }

    @Override
    public int getVanishPriority(Player player) {
        return canVanish(player) ? 1 : 0;
    }

    @Override
    public int getViewPriority(Player player) {
        return canVanish(player) ? 1 : 0;
    }

        private User getUser(Player player) {
        return api.getUser(player);
    }
}
