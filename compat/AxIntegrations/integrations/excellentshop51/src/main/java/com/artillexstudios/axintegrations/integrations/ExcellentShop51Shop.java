package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ShopIntegration;
import com.artillexstudios.axintegrations.utils.ExcellentShopReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ExcellentShop51Shop extends ShopIntegration {
    private final ExcellentShopReflection shop = new ExcellentShopReflection(
            "su.nightexpress.excellentshop.virtualshop.VirtualShopModule",
            "su.nightexpress.excellentshop.api.product.TradeType", null, false);

    public ExcellentShop51Shop() {
        super("ExcellentShop");
    }

    @Override
    public boolean canLoad() {
        return shop.canLoad();
    }

    @Override
    public boolean setup() {
        return shop.setup();
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public Double getBuyPrice(ItemStack item) {
        return shop.getPrice(copy(item), "BUY", null);
    }

    @Override
    public Double getBuyPrice(UUID playerUUID, ItemStack item) {
        Player player = Bukkit.getPlayer(playerUUID);
        return player == null ? getBuyPrice(item) : shop.getPrice(copy(item), "BUY", player);
    }

    @Override
    public Double getSellPrice(ItemStack item) {
        return shop.getPrice(copy(item), "SELL", null);
    }

    @Override
    public Double getSellPrice(UUID playerUUID, ItemStack item) {
        Player player = Bukkit.getPlayer(playerUUID);
        return player == null ? getSellPrice(item) : shop.getPrice(copy(item), "SELL", player);
    }
}
