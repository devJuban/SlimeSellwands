package com.artillexstudios.axsellwands.hooks;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axsellwands.AxSellwands;
import com.artillexstudios.axsellwands.hooks.integration.ShopIntegration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.UUID;

public class BuiltinPrices extends ShopIntegration {
    public static BuiltinPrices INSTANCE;
    private static Config PRICES;

    public BuiltinPrices() {
        super("builtin");
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    public void reload() {
        if (PRICES != null) PRICES.reload();
    }

    @Override
    public boolean setup() {
        if (PRICES == null) PRICES = new Config(new File(AxSellwands.getInstance().getDataFolder(), "prices.yml"), AxSellwands.getInstance().getResource("prices.yml"));
        INSTANCE = this;
        return PRICES.getBackingDocument() != null;
    }

    @Override
    public @Nullable Double getBuyPrice(@NotNull ItemStack itemStack) {
        return getSellPrice(itemStack);
    }

    @Override
    public @Nullable Double getBuyPrice(UUID uuid, @NotNull ItemStack itemStack) {
        return getSellPrice(uuid, itemStack);
    }

    @Override
    public @Nullable Double getSellPrice(@NotNull ItemStack itemStack) {
        Double price = PRICES.getBackingDocument().getDouble(itemStack.getType().name());
        if (price == null || price <= 0) return null;
        return price * itemStack.getAmount();
    }

    @Override
    public @Nullable Double getSellPrice(UUID uuid, @NotNull ItemStack itemStack) {
        return getSellPrice(itemStack);
    }
}