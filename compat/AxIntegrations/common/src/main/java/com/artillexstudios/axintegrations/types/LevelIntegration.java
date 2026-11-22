package com.artillexstudios.axintegrations.types;

import com.artillexstudios.axintegrations.Integration;
import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.IntegrationType;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Rules
 * - if the plugin doesn't support XP, return 0
 */
public abstract class LevelIntegration extends Integration {

    /**
     * returns all loaded integrations
     */
    public static List<LevelIntegration> list() {
        return IntegrationManager.getIntegrations(LevelIntegration.class);
    }

    /**
     * returns a loaded integration
     * if you register multiple, you should use {@link this#list()} instead
     */
        public static LevelIntegration one() {
        return list().stream().findFirst().orElse(null);
    }

    /**
     * returns a loaded integration by name
     */
        public static LevelIntegration one(String name) {
        return list().stream().filter(i -> {
            return i.getName().equalsIgnoreCase(name) || i.getFormattedName().equalsIgnoreCase(name);
        }).findFirst().orElse(null);
    }

    public LevelIntegration(String name) {
        super(name, IntegrationType.LEVEL);
    }

    public abstract long getLevel(UUID playerUUID);

    public abstract double getXP(UUID playerUUID);

    /**
     * required xp for the next level
     */
    public abstract double getRequiredXP(UUID playerUUID);

    /**
     * the amount of xp the playerUUID needs to get to level up
     */
    public abstract double getRemainingXP(UUID playerUUID);

    /**
     * reset level and xp
     */
    public CompletableFuture<Boolean> reset(UUID playerUUID) {
        setXP(playerUUID, 0);
        return setLevel(playerUUID, 0);
    }

    public abstract CompletableFuture<Boolean> setLevel(UUID playerUUID, long amount);

    public CompletableFuture<Boolean> giveLevel(UUID playerUUID, long amount) {
        return setLevel(playerUUID, getLevel(playerUUID) + amount);
    }

    public CompletableFuture<Boolean> takeLevel(UUID playerUUID, long amount) {
        return setLevel(playerUUID, getLevel(playerUUID) - amount);
    }

    public abstract CompletableFuture<Boolean> setXP(UUID playerUUID, double amount);

    public CompletableFuture<Boolean> giveXP(UUID playerUUID, double amount) {
        return setXP(playerUUID, getXP(playerUUID) + amount);
    }

    public CompletableFuture<Boolean> takeXP(UUID playerUUID, double amount) {
        return setXP(playerUUID, getXP(playerUUID) - amount);
    }
}
