package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class VaultCurrency extends CurrencyIntegration {
    private Economy api;

    public VaultCurrency() {
        super("Vault", null);
    }

    @Override
    public boolean canLoad() {
        try {
            Class.forName("net.milkbowl.vault.economy.Economy", false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (provider == null) return false;
        api = provider.getProvider();
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
        return api.getBalance(player);
    }

        @Override
    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        return CompletableFuture.completedFuture(api.getBalance(offlinePlayer));
    }

        @Override
    public CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        EconomyResponse response = api.depositPlayer(offlinePlayer, amount);
        return CompletableFuture.completedFuture(response.transactionSuccess());
    }

        @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
        EconomyResponse response = api.withdrawPlayer(offlinePlayer, amount);
        return CompletableFuture.completedFuture(response.transactionSuccess());
    }
}
