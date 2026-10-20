package com.artillexstudios.axintegrations.types;

import com.artillexstudios.axintegrations.Integration;
import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.IntegrationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.List;

/**
 * Rules
 * - every method must return true if the player has bypass permission
 */
public abstract class ProtectionIntegration extends Integration {

    /**
     * returns all loaded integrations
     */
    public static List<ProtectionIntegration> list() {
        return IntegrationManager.getIntegrations(ProtectionIntegration.class);
    }

    /**
     * returns a loaded integration
     * if you register multiple, you should use {@link this#list()} instead
     */
        public static ProtectionIntegration one() {
        return list().stream().findFirst().orElse(null);
    }

    /**
     * returns a loaded integration by name
     */
        public static ProtectionIntegration one(String name) {
        return list().stream().filter(i -> {
            return i.getName().equalsIgnoreCase(name) || i.getFormattedName().equalsIgnoreCase(name);
        }).findFirst().orElse(null);
    }

    public enum Permission {
        PLACE,
        BREAK,
        INTERACT,
        OPEN_CONTAINER
    }

    public static boolean hasPermission(Player player, Location location, Permission permission) {
        for (ProtectionIntegration protectionIntegration : list()) {
            switch (permission) {
                case PLACE:
                    if (!protectionIntegration.canPlace(player, location)) return false;
                    break;
                case BREAK:
                    if (!protectionIntegration.canBreak(player, location)) return false;
                    break;
                case INTERACT:
                    if (!protectionIntegration.canInteract(player, location)) return false;
                    break;
                case OPEN_CONTAINER:
                    if (!protectionIntegration.canOpenContainer(player, location)) return false;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown protection permission: " + permission);
            }
        }
        return true;
    }

    public ProtectionIntegration(String name) {
        super(name, IntegrationType.PROTECTION);
    }

    public abstract boolean canPlace(Player player, Location location);

    public abstract boolean canBreak(Player player, Location location);

    public abstract boolean canInteract(Player player, Location location);

    public abstract boolean canOpenContainer(Player player, Location location);
}
