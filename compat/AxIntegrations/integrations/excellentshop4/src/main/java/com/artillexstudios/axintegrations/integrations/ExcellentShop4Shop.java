package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axintegrations.types.ShopIntegration;
import com.artillexstudios.axintegrations.utils.ExcellentShopReflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ExcellentShop4Shop extends ShopIntegration {
    private final ExcellentShopReflection shop = new ExcellentShopReflection(
            "su.nightexpress.nexshop.shop.virtual.VirtualShopModule",
            "su.nightexpress.nexshop.api.shop.type.TradeType",
            "su.nightexpress.nexshop.ShopAPI", true);

    public ExcellentShop4Shop() {
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
