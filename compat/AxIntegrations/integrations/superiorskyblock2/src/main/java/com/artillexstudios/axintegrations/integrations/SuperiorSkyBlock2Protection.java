package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public class SuperiorSkyBlock2Protection extends ProtectionIntegration {

    public SuperiorSkyBlock2Protection() {
        super("SuperiorSkyBlock2");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return hasPermission(player, location, IslandPrivilege.getByName("BUILD"));
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return hasPermission(player, location, IslandPrivilege.getByName("BREAK"));
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return hasPermission(player, location, IslandPrivilege.getByName("INTERACT"));
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return hasPermission(player, location, IslandPrivilege.getByName("CHEST_ACCESS"));
    }

    private boolean hasPermission(Player player, Location location, IslandPrivilege privilege) {
        Island island = SuperiorSkyblockAPI.getIslandAt(location);
        if (island == null) return true;
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        if (superiorPlayer == null) return true;
        return island.hasPermission(superiorPlayer, privilege);
    }
}
