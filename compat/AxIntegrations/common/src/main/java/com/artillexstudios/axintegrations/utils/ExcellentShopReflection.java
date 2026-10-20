package com.artillexstudios.axintegrations.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Reflection bridge for ExcellentShop releases whose public API packages differ. */
public final class ExcellentShopReflection {
    private final String moduleClassName;
    private final String tradeTypeClassName;
    private final String staticShopApiClassName;
    private final boolean playerAwareProductLookup;
    private Plugin plugin;
    private Object module;

    public ExcellentShopReflection(String moduleClassName, String tradeTypeClassName,
                                   String staticShopApiClassName, boolean playerAwareProductLookup) {
        this.moduleClassName = moduleClassName;
        this.tradeTypeClassName = tradeTypeClassName;
        this.staticShopApiClassName = staticShopApiClassName;
        this.playerAwareProductLookup = playerAwareProductLookup;
    }

    public boolean canLoad() {
        Plugin candidate = Bukkit.getPluginManager().getPlugin("ExcellentShop");
        if (candidate == null || !candidate.isEnabled()) return false;
        try {
            Class.forName(moduleClassName, false, candidate.getClass().getClassLoader());
            plugin = candidate;
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    public boolean setup() {
        if (plugin == null) plugin = Bukkit.getPluginManager().getPlugin("ExcellentShop");
        if (plugin == null) return false;
        try {
            ClassLoader loader = plugin.getClass().getClassLoader();
            Class<?> moduleType = Class.forName(moduleClassName, true, loader);
            if (staticShopApiClassName != null) {
                Class<?> apiType = Class.forName(staticShopApiClassName, true, loader);
                module = invokeStatic(apiType, "getVirtualShop");
            } else {
                Object registry = invoke(plugin, "getModuleRegistry");
                Object optional = invoke(registry, "byType", moduleType);
                module = invoke(optional, "orElse", (Object) null);
            }
            return module != null;
        } catch (ReflectiveOperationException | LinkageError error) {
            return false;
        }
    }

    public Double getPrice(ItemStack item, String tradeName, Player player) {
        if (module == null || item == null) return null;
        try {
            Class<?> tradeType = Class.forName(tradeTypeClassName, true, plugin.getClass().getClassLoader());
            Object trade = Enum.valueOf((Class<? extends Enum>) tradeType.asSubclass(Enum.class), tradeName);
            Object product;
            Method lookupWithPlayer = playerAwareProductLookup && player != null
                    ? findMethod(module.getClass(), "getBestProductFor", item, trade, player) : null;
            if (lookupWithPlayer != null) {
                product = lookupWithPlayer.invoke(module, item, trade, player);
            } else {
                product = invoke(module, "getBestProductFor", item, trade);
            }
            if (product == null) return null;

            Method priceWithPlayer = player != null
                    ? findMethod(product.getClass(), "getFinalPrice", trade, item.getAmount(), player) : null;
            Object result = priceWithPlayer == null
                    ? invoke(product, "getFinalPrice", trade, item.getAmount())
                    : priceWithPlayer.invoke(product, trade, item.getAmount(), player);
            return result instanceof Number ? ((Number) result).doubleValue() : null;
        } catch (ReflectiveOperationException | LinkageError | IllegalArgumentException ignored) {
            return null;
        }
    }

    private static Object invokeStatic(Class<?> owner, String name, Object... arguments) throws ReflectiveOperationException {
        Method method = findMethod(owner, name, arguments);
        if (method == null || !Modifier.isStatic(method.getModifiers())) {
            throw new NoSuchMethodException(owner.getName() + "." + name);
        }
        return method.invoke(null, arguments);
    }

    private static Object invoke(Object target, String name, Object... arguments) throws ReflectiveOperationException {
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
