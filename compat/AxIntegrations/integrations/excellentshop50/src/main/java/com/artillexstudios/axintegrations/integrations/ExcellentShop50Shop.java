package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ShopIntegration;
import com.artillexstudios.axintegrations.utils.ExcellentShopReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ExcellentShop50Shop extends ShopIntegration {
    private final ExcellentShopReflection shop = new ExcellentShopReflection(
            "su.nightexpress.excellentshop.feature.virtualshop.VirtualShopModule",
            "su.nightexpress.excellentshop.api.product.TradeType", null, true);

    public ExcellentShop50Shop() {
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
