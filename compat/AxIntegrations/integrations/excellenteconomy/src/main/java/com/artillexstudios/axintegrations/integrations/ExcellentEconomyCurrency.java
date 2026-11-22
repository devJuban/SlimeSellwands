package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/** Optional ExcellentEconomy adapter without a hard provider API dependency. */
public class ExcellentEconomyCurrency extends CurrencyIntegration {
    private static final String API_CLASS = "su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI";
    private Class<?> apiType;
    private Object api;
    private Object currency;

    public ExcellentEconomyCurrency(String currency) {
        super("ExcellentEconomy", currency);
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("ExcellentEconomy");
        if (plugin == null || !plugin.isEnabled()) return false;
        try {
            apiType = Class.forName(API_CLASS, false, plugin.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public boolean setup() {
        String name = getCurrency();
        if (name == null) {
            IntegrationManager.print(false, String.format("Failed to register %s! Currency not set!", getName()));
            return false;
        }
        try {
            Object registration = call(Bukkit.getServer().getServicesManager(), "getRegistration", apiType);
            if (registration == null) return false;
            api = call(registration, "getProvider");
            currency = call(api, "getCurrency", name);
            if (currency == null) {
                IntegrationManager.print(false, String.format("Failed to register %s! Currency '%s' not found!", getName(), name));
                return false;
            }
            return true;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not initialize ExcellentEconomy currency " + name, error);
        }
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
        try {
            return Boolean.TRUE.equals(call(currency, "isDecimal"));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read ExcellentEconomy currency precision", error);
        }
    }

    @Override
    public double getBalance(Player player) {
        try {
            return ((Number) call(api, "getBalance", player, currency)).doubleValue();
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read ExcellentEconomy balance", error);
        }
    }

    @Override
    public CompletableFuture<Double> getBalance(UUID playerUUID) {
        try {
            return numberFuture(call(api, "getBalanceAsync", playerUUID, currency));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read ExcellentEconomy balance", error);
        }
    }

    @Override
    public CompletableFuture<Boolean> giveBalance(UUID playerUUID, double amount) {
        try {
            return successFuture(call(api, "depositAsync", playerUUID, currency, amount));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not deposit ExcellentEconomy balance", error);
        }
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        try {
            return successFuture(call(api, "withdrawAsync", playerUUID, currency, amount));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not withdraw ExcellentEconomy balance", error);
        }
    }

    @SuppressWarnings("unchecked")
    private static CompletableFuture<Double> numberFuture(Object value) {
        return ((CompletableFuture<Number>) value).thenApply(Number::doubleValue);
    }

    @SuppressWarnings("unchecked")
    private static CompletableFuture<Boolean> successFuture(Object value) {
        return ((CompletableFuture<Object>) value).thenApply(result -> {
            try {
                return Boolean.TRUE.equals(call(result, "success"));
            } catch (ReflectiveOperationException error) {
                throw new CompletionException(error);
            }
        });
    }

    private static Object call(Object target, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(target.getClass(), name, arguments);
        if (method == null) throw new NoSuchMethodException(target.getClass().getName() + "." + name);
        return method.invoke(target, arguments);
    }

    private static Method findMethod(Class<?> owner, String name, Object... arguments) {
        for (Method method : owner.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean matches = true;
            for (int index = 0; index < parameters.length; index++) {
                if (arguments[index] != null && !wrap(parameters[index]).isAssignableFrom(arguments[index].getClass())) {
                    matches = false;
                    break;
                }
            }
            if (matches) return method;
        }
        return null;
    }

    private static Class<?> wrap(Class<?> type) {
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == long.class) return Long.class;
        if (type == int.class) return Integer.class;
        if (type == boolean.class) return Boolean.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        return type;
    }
}
