package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import me.mraxetv.beasttokens.api.BeastTokensAPI;
import me.mraxetv.beasttokens.api.handlers.BTTokensManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BeastTokensCurrency extends CurrencyIntegration {
    private BTTokensManager manager;

    public BeastTokensCurrency() {
        super("BeastTokens", null);
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.mraxetv.beasttokens.api.BeastTokensAPI");
    }

    @Override
    public boolean setup() {
        manager = BeastTokensAPI.getTokensManager();
        if (manager == null) return false;
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean worksOffline() {
        return true;
    }

    @Override
    public boolean usesDecimals() {
        return true;
    }

    @Override
    public double getBalance(Player player) {
        return manager.getTokens(player);
    }

        @Override
    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        return CompletableFuture.completedFuture(manager.getTokens(offlinePlayer));
    }

        @Override
    public CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        manager.addTokens(offlinePlayer, amount);
        return CompletableFuture.completedFuture(true);
    }

        @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        manager.removeTokens(offlinePlayer, amount);
        return CompletableFuture.completedFuture(true);
    }
}
