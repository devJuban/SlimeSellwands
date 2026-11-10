package com.artillexstudios.axsellwands.hooks.integration;

import com.artillexstudios.axsellwands.AxSellwands;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Method;
import java.util.UUID;

public final class CurrencyIntegration {
    private final Object economy;
    private final Method deposit;
    private CurrencyIntegration(Object economy, Method deposit) { this.economy = economy; this.deposit = deposit; }

    public static CurrencyIntegration one() {
        if (AxSellwands.HOOKS == null || !"vault".equalsIgnoreCase(AxSellwands.HOOKS.getString("hooks.economy-plugin", "Vault"))) return null;
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return null;
        try {
            Class<?> economyType = Class.forName("net.milkbowl.vault.economy.Economy");
            Object registration = Bukkit.getServicesManager().getRegistration(economyType);
            if (registration == null) return null;
            Object provider = registration.getClass().getMethod("getProvider").invoke(registration);
            return new CurrencyIntegration(provider, economyType.getMethod("depositPlayer", OfflinePlayer.class, double.class));
        } catch (Throwable ignored) { return null; }
    }

    public boolean giveBalance(UUID player, double amount) {
        try {
            Object response = deposit.invoke(economy, Bukkit.getOfflinePlayer(player), amount);
            if (response == null) return false;
            Object success = response.getClass().getMethod("transactionSuccess").invoke(response);
            return Boolean.TRUE.equals(success);
        }
        catch (ReflectiveOperationException ex) { throw new IllegalStateException("Vault deposit failed", ex); }
    }
}
