package com.artillexstudios.axintegrations.types;

import com.artillexstudios.axintegrations.Integration;
import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.IntegrationType;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Rules
 * - currency is null unless it is a multi currency supporting plugin
 * - if {@link this#worksOffline()} is false but an offline player is queried or not found, return false or 0 with {@link CompletableFuture}
 */
public abstract class CurrencyIntegration extends Integration {
    private final String currency;

    /**
     * returns all loaded integrations
     */
    public static List<CurrencyIntegration> list() {
        return IntegrationManager.getIntegrations(CurrencyIntegration.class);
    }

    /**
     * returns a loaded integration
     * if you register multiple, you should use {@link this#list()} instead
     */
        public static CurrencyIntegration one() {
        return list().stream().findFirst().orElse(null);
    }

    /**
     * returns a loaded integration by name
     */
        public static CurrencyIntegration one(String name) {
        return list().stream().filter(i -> {
            return i.getName().equalsIgnoreCase(name) || i.getFormattedName().equalsIgnoreCase(name);
        }).findFirst().orElse(null);
    }

    public CurrencyIntegration(String name, String currency) {
        super(name, IntegrationType.CURRENCY);
        this.currency = currency;
    }

        public String getCurrency() {
        return currency;
    }

    @Override
        public final String getFormattedName() {
        if (currency == null) return getName();
        return String.format("%s-%s", getName(), currency);
    }

    /**
     * does this integration work on offline players?
     */
    public abstract boolean worksOffline();

    /**
     * does this integration work use double or float?
     */
    public abstract boolean usesDecimals();

    /**
     * use {@link this#getBalance(UUID)} if possible
     * this method is just to retrieve cached balance
     * only use this to display balance, don't allow purchases just by using this
     */
    public abstract double getBalance(Player player);

        public abstract CompletableFuture<Double> getBalance(UUID playerUUID);

        public abstract CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount);

        public abstract CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount);
}
