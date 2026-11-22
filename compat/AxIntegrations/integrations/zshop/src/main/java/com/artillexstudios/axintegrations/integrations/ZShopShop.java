package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.ShopIntegration;
import fr.maxlego08.zshop.api.ShopManager;
import fr.maxlego08.zshop.api.buttons.ItemButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import java.util.UUID;

public class ZShopShop extends ShopIntegration {
    private ShopManager manager;

    public ZShopShop() {
        super("zShop");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("fr.maxlego08.zshop.api.ShopManager");
    }

    @Override
    public boolean setup() {
        RegisteredServiceProvider<ShopManager> shopManager = Bukkit.getServicesManager().getRegistration(ShopManager.class);
        if (shopManager == null) return false;
        manager = shopManager.getProvider();
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

        @Override
    public Double getBuyPrice(ItemStack item) {
        ItemButton button = manager.getItemButton(item.getType()).orElse(null);
        if (button == null) return null;
        return button.getBuyPrice(item.getAmount());
    }

        @Override
    public Double getBuyPrice(UUID playerUUID, ItemStack item) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return getBuyPrice(item);
        ItemButton button = manager.getItemButton(item.getType()).orElse(null);
        if (button == null) return null;
        return button.getBuyPrice(player, item.getAmount());
    }

        @Override
    public Double getSellPrice(ItemStack item) {
        ItemButton button = manager.getItemButton(item.getType()).orElse(null);
        if (button == null) return null;
        return button.getSellPrice(item.getAmount());
    }

        @Override
    public Double getSellPrice(UUID playerUUID, ItemStack item) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return getSellPrice(item);
        ItemButton button = manager.getItemButton(item.getType()).orElse(null);
        if (button == null) return null;
        return button.getSellPrice(player, item.getAmount());
    }
}
