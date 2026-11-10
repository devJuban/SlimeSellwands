package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import me.qKing12.RoyaleEconomy.API.APIHandler;
import me.qKing12.RoyaleEconomy.RoyaleEconomy;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RoyaleEconomyCurrency extends CurrencyIntegration {
    private APIHandler api;

    public RoyaleEconomyCurrency(String currency) {
        super("RoyaleEconomy", currency);
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("me.qKing12.RoyaleEconomy.API.APIHandler");
    }

    @Override
    public boolean setup() {
        api = RoyaleEconomy.apiHandler;
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
        return api.balance.getBalance(player.getUniqueId().toString());
    }

        @Override
    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        return CompletableFuture.completedFuture(api.balance.getBalance(playerUUID.toString()));
    }

        @Override
    public CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount) {
        api.balance.addBalance(playerUUID.toString(), amount);
        return CompletableFuture.completedFuture(true);
    }

        @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        api.balance.removeBalance(playerUUID.toString(), amount);
        return CompletableFuture.completedFuture(true);
    }
}
