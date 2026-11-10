package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LevelCurrency extends CurrencyIntegration {

    public LevelCurrency() {
        super("Level", null);
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean worksOffline() {
        return false;
    }

    @Override
    public boolean usesDecimals() {
        return false;
    }

    @Override
    public double getBalance(Player player) {
        return (double) player.getLevel();
    }

        @Override
    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return CompletableFuture.completedFuture(0D);
        return CompletableFuture.completedFuture(getBalance(player));
    }

        @Override
    public CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return CompletableFuture.completedFuture(false);
        player.setLevel((int) (player.getLevel() + amount));
        return CompletableFuture.completedFuture(true);
    }

        @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return CompletableFuture.completedFuture(false);
        player.setLevel((int) (player.getLevel() - amount));
        return CompletableFuture.completedFuture(true);
    }
}
