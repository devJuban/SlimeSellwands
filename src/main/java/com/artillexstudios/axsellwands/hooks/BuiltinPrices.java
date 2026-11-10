package com.artillexstudios.axsellwands.hooks;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axsellwands.AxSellwands;
import com.artillexstudios.axsellwands.hooks.integration.ShopIntegration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.UUID;

public class BuiltinPrices extends ShopIntegration {
    private Config PRICES;

    public BuiltinPrices() {
        super("builtin");
    }

    @Override
    public boolean canLoad() {
        return true;
    }

    @Override
    public boolean setup() {
        PRICES = new Config(new File(AxSellwands.getInstance().getDataFolder(), "prices.yml"), AxSellwands.getInstance().getResource("prices.yml"));
        return PRICES.getBackingDocument() != null;
    }

    @Override
    public Double getBuyPrice(ItemStack itemStack) {
        return getSellPrice(itemStack);
    }

    @Override
    public Double getBuyPrice(UUID uuid, ItemStack itemStack) {
        return getSellPrice(uuid, itemStack);
    }

    @Override
    public Double getSellPrice(ItemStack itemStack) {
        Double price = getPrice(itemStack.getType().name());
        if ((price == null || price <= 0) && itemStack.getType().name().equals("LOG")) {
            String[] logs = {"OAK_LOG", "SPRUCE_LOG", "BIRCH_LOG", "JUNGLE_LOG"};
            int data = itemStack.getDurability() & 3;
            price = getPrice(logs[data]);
        } else if ((price == null || price <= 0) && itemStack.getType().name().equals("LOG_2")) {
            String[] logs = {"ACACIA_LOG", "DARK_OAK_LOG"};
            int data = itemStack.getDurability() & 1;
            price = getPrice(logs[data]);
        }
        if (price == null || price <= 0) return null;
        return price * itemStack.getAmount();
    }

    @Override
    public Double getSellPrice(UUID uuid, ItemStack itemStack) {
        return getSellPrice(itemStack);
    }

    private Double getPrice(String material) {
        double value = PRICES.getBackingDocument().getDouble(material);
        return value <= 0 ? null : value;
    }
}
