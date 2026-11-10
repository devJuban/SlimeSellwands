package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import fr.xyness.SCS.API.SimpleClaimSystemAPI;
import fr.xyness.SCS.API.SimpleClaimSystemAPI_Provider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
public class SimpleClaimSystemProtection extends ProtectionIntegration {
    private SimpleClaimSystemAPI api;

    public SimpleClaimSystemProtection() {
        super("SimpleClaimSystem");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("fr.xyness.SCS.API.SimpleClaimSystemAPI");
    }

    @Override
    public boolean setup() {
        try {
            api = SimpleClaimSystemAPI_Provider.getAPI();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        return isAllowed(player, location, "Build");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        return isAllowed(player, location, "Destroy");
    }

    @Override
    public boolean canInteract(Player player, Location location) {
        return isAllowed(player, location, "Build");
    }

    @Override
    public boolean canOpenContainer(Player player, Location location) {
        return isAllowed(player, location, "InteractBlocks");
    }

    private boolean isAllowed(Player player, Location location, String permission) {
        return api.getClaimAtChunk(location.getChunk()).getPermissionForPlayer(permission, player);
    }
}
