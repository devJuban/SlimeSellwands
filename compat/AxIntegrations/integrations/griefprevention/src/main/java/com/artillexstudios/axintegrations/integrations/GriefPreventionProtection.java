package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public class GriefPreventionProtection extends ProtectionIntegration {

    public GriefPreventionProtection() {
        super("GriefPrevention");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.ryanhamshire.GriefPrevention.GriefPrevention");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return isAllowed(player, location);
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return isAllowed(player, location);
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return isAllowed(player, location);
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return isAllowed(player, location);
    }

    private boolean isAllowed(Player player, Location location) {
        return GriefPrevention.instance.allowBuild(player, location) == null; // todo: use permissions?
    }
}
