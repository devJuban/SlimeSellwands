package com.artillexstudios.axsellwands.hooks.integration;

import com.artillexstudios.axsellwands.AxSellwands;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class ShopIntegration {
    private static ShopIntegration builtin;
    private final String name;
    protected ShopIntegration(String name) { this.name = name; }
    public String getName() { return name; }
    public abstract boolean canLoad();
    public abstract boolean setup();
    public abstract Double getSellPrice(ItemStack itemStack);
    public abstract Double getSellPrice(UUID uuid, ItemStack itemStack);
    public abstract Double getBuyPrice(ItemStack itemStack);
    public abstract Double getBuyPrice(UUID uuid, ItemStack itemStack);
    public static void register(ShopIntegration integration) { if ("builtin".equalsIgnoreCase(integration.getName())) builtin = integration; }
    public static ShopIntegration one() {
        return builtin != null && AxSellwands.HOOKS != null
                && "builtin".equalsIgnoreCase(AxSellwands.HOOKS.getString("hooks.price-plugin", "builtin")) ? builtin : null;
    }
}
