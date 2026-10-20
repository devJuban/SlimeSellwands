package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import me.rivaldev.credits.CreditAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RivalCreditsCurrency extends CurrencyIntegration {
    private CreditAPI api;

    public RivalCreditsCurrency() {
        super("RivalCredits", null);
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.rivaldev.credits.CreditAPI");
    }

    @Override
    public boolean setup() {
        api = CreditAPI.getInstance();
        if (api == null) return false;
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
        return api.getCredits(player);
    }

        @Override
    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        return CompletableFuture.completedFuture(api.getCredits(offlinePlayer));
    }

        @Override
    public CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        api.addCredits(offlinePlayer, amount);
        return CompletableFuture.completedFuture(true);
    }

        @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        api.removeCredits(offlinePlayer, amount);
        return CompletableFuture.completedFuture(true);
    }
}
