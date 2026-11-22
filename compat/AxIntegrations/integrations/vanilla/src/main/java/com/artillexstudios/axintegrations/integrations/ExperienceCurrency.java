package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import com.artillexstudios.axintegrations.utils.ExperienceUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ExperienceCurrency extends CurrencyIntegration {

    public ExperienceCurrency() {
        super("Experience", null);
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
        return ExperienceUtils.getExp(player);
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
        ExperienceUtils.changeExp(player, (int) amount);
        return CompletableFuture.completedFuture(true);
    }

        @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return CompletableFuture.completedFuture(false);
        ExperienceUtils.changeExp(player, (int) -amount);
        return CompletableFuture.completedFuture(true);
    }
}
