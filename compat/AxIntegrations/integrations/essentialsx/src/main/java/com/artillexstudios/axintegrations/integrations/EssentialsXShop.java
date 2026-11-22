package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ShopIntegration;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Worth;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import java.math.BigDecimal;
import java.util.UUID;

public class EssentialsXShop extends ShopIntegration {
    private BigDecimal negative = BigDecimal.ONE.negate();
    private IEssentials api;
    private Worth worth;

    public EssentialsXShop() {
        super("EssentialsX");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.earth2me.essentials.IEssentials");
    }

    @Override
    public boolean setup() {
        api = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
        if (api == null) return false;
        worth = api.getWorth();
        if (worth == null) return false;
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

        @Override
    public Double getBuyPrice(ItemStack item) {
        return getSellPrice(item);
    }

        @Override
    public Double getBuyPrice(UUID playerUUID, ItemStack item) {
        return getSellPrice(playerUUID, item);
    }

        @Override
    public Double getSellPrice(ItemStack item) {
        BigDecimal price = worth.getPrice(api, copy(item));
        if (price == null || price.equals(negative)) return null;
        return price.doubleValue() * item.getAmount();
    }

        @Override
    public Double getSellPrice(UUID playerUUID, ItemStack item) {
        return getSellPrice(item);
    }
}
