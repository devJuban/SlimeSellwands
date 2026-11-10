package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * CoinsEngine adapter kept reflective so the optional provider API does not
 * force a modern CoinsEngine artifact onto legacy Java 8 builds.
 */
public class CoinsEngineCurrency extends CurrencyIntegration {
    private static final String API_CLASS = "su.nightexpress.coinsengine.api.CoinsEngineAPI";
    private Class<?> api;
    private ClassLoader providerLoader;
    private Object currency;

    public CoinsEngineCurrency(String currency) {
        super("CoinsEngine", currency);
    }

    @Override
    public boolean canLoad() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CoinsEngine");
        if (plugin == null || !plugin.isEnabled()) return false;
        try {
            providerLoader = plugin.getClass().getClassLoader();
            api = Class.forName(API_CLASS, false, providerLoader);
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
            currency = invokeStatic("getCurrency", name);
            if (currency == null) {
                IntegrationManager.print(false, String.format("Failed to register %s! Currency '%s' not found!", getName(), name));
                return false;
            }
            return true;
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not query CoinsEngine currency " + name, error);
        }
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
        try {
            Method method = findMethod(currency.getClass(), "isDecimal");
            return method != null && Boolean.TRUE.equals(method.invoke(currency));
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read CoinsEngine currency precision", error);
        }
    }

    @Override
    public double getBalance(Player player) {
        try {
            Object value = invokeStatic("getBalance", player, currency);
            return ((Number) value).doubleValue();
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not read CoinsEngine balance", error);
        }
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
        try {
            invokeStatic("addBalance", player, currency, amount);
            return CompletableFuture.completedFuture(true);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not add CoinsEngine balance", error);
        }
    }

    @Override
    public CompletableFuture<Boolean> takeBalance(UUID playerUUID, double amount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return CompletableFuture.completedFuture(false);
        try {
            invokeStatic("removeBalance", player, currency, amount);
            return CompletableFuture.completedFuture(true);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not remove CoinsEngine balance", error);
        }
    }

    private Object invokeStatic(String name, Object... arguments) throws ReflectiveOperationException {
        if (api == null) api = Class.forName(API_CLASS, true, providerLoader);
        Method method = findMethod(api, name, arguments);
        if (method == null) throw new NoSuchMethodException(api.getName() + "." + name);
        return method.invoke(null, arguments);
    }

    private static Method findMethod(Class<?> owner, String name, Object... arguments) {
        for (Method method : owner.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterTypes().length != arguments.length) continue;
            if (!Modifier.isStatic(method.getModifiers())) continue;
            Class<?>[] parameters = method.getParameterTypes();
            boolean matches = true;
            for (int index = 0; index < parameters.length; index++) {
                Object argument = arguments[index];
                if (argument != null && !wrap(parameters[index]).isAssignableFrom(argument.getClass())) {
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
