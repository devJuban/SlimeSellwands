package com.artillexstudios.axsellwands.listeners;

import com.artillexstudios.axapi.items.NBTWrapper;
import com.artillexstudios.axapi.utils.*;
import com.artillexstudios.axintegrations.types.CurrencyIntegration;
import com.artillexstudios.axintegrations.types.ProtectionIntegration;
import com.artillexstudios.axintegrations.types.ShopIntegration;
import com.artillexstudios.axsellwands.api.events.AxSellwandsSellEvent;
import com.artillexstudios.axsellwands.sellwands.Sellwand;
import com.artillexstudios.axsellwands.sellwands.Sellwands;
import com.artillexstudios.axsellwands.utils.HistoryUtils;
import com.artillexstudios.axsellwands.utils.HologramUtils;
import com.artillexstudios.axsellwands.utils.NumberUtils;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnit;
import io.ncbpfluffybear.fluffymachines.items.Barrel;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import io.github.mooy1.infinityexpansion.items.storage.StorageUnitAPI;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static com.artillexstudios.axsellwands.AxSellwands.*;
import static com.artillexstudios.axsellwands.AxSellwands.BIE;
import static com.artillexstudios.axsellwands.AxSellwands.IE;
import static com.artillexstudios.axsellwands.AxSellwands.FM;

public class SellwandUseListener implements Listener {

    static int[] OUTPUT_SLOTS = {24,25};
    static int IE_OUTPUT_SLOT = 16;

    @EventHandler(ignoreCancelled = true)
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        NBTWrapper wrapper = new NBTWrapper(event.getItem());
        String type = wrapper.getString("axsellwands-type");
        if (type == null) return;
        Sellwand sellwand = Sellwands.getSellwands().get(type);
        event.setCancelled(true);
        if (sellwand == null) return;
        Player player = event.getPlayer();

        ItemStack[] contents;
        if (block.getState() instanceof Container && !isSlimefunItem(block)) {
            contents = ((Container) block.getState()).getInventory().getContents();
        } else if (block.getType() == Material.ENDER_CHEST) {
            contents = player.getEnderChest().getContents();
        } else if (CONFIG.getBoolean("slimefun-integration") && getPlugin("Slimefun") != null){

            if (isUnit(block)){
                SimpleEntry<StorageUnit, Location> data = getUnitBySign(block);
                if (data == null) return;
                Block b = data.getValue().getBlock();

                BlockMenu menu = BlockStorage.getInventory(b);
                if (menu == null) return;
                if (menu.hasViewer()) {
                    MESSAGEUTILS.sendLang(player, "block-has-viewer");
                    return;
                }

                contents = new ItemStack[]{getContents(b)};
            } else if (isBarrel(block)) {
                BlockMenu menu = BlockStorage.getInventory(block);
                if (menu == null) return;
                if (menu.hasViewer()) {
                    MESSAGEUTILS.sendLang(player, "block-has-viewer");
                    return;
                }
                contents = new ItemStack[]{getBarrelContents(block)};
            } else return;
        } else {
            return; // not a container
        }

        boolean hasBypass = player.hasPermission("axsellwands.admin");

        if (!hasBypass && !ProtectionIntegration.hasPermission(player, block.getLocation(), ProtectionIntegration.Permission.BREAK)) {
            MESSAGEUTILS.sendLang(player, "no-permission");
            return;
        }

        if (sellwand.getDisallowed().contains(block.getType()) || (!sellwand.getAllowed().isEmpty() && !sellwand.getAllowed().contains(block.getType()))) {
            MESSAGEUTILS.sendLang(player, "disallowed-container");
            return;
        }

        Long lastUsed = wrapper.getLong("axsellwands-lastused");
        if (lastUsed != null && System.currentTimeMillis() - lastUsed < sellwand.getCooldown() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            MESSAGEUTILS.sendLang(player, "cooldown", Collections.singletonMap("%time%", Long.toString(Math.round((sellwand.getCooldown() - System.currentTimeMillis() + lastUsed) / 1000D))));
            return;
        }

        UUID uuid = wrapper.getUUID("axsellwands-uuid");
        float multiplier = wrapper.getFloatOr("axsellwands-multiplier", 1);
        int uses = wrapper.getIntOr("axsellwands-uses", -1);
        int maxUses = wrapper.getIntOr("axsellwands-max-uses", -1);
        int soldAmount = wrapper.getIntOr("axsellwands-sold-amount", 0);
        double soldPrice = wrapper.getDoubleOr("axsellwands-sold-price", 0);

        int newSoldAmount = 0;
        double newSoldPrice = 0;

        ShopIntegration shopIntegration = ShopIntegration.one();
        if (shopIntegration == null) {
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxSellwands] Failed to sell items, no shop plugin selected in the hooks.yml!"));
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Map<Material, Integer> items = new HashMap<>();
            for (ItemStack it : contents) {
                if (it == null) continue;
                Double price = shopIntegration.getSellPrice(player.getUniqueId(), it);
                if (price == null || price <= 0) continue;
                price *= multiplier;

                newSoldPrice += price;
                newSoldAmount += it.getAmount();

                if (items.containsKey(it.getType())) {
                    items.put(it.getType(), items.get(it.getType()) + it.getAmount());
                } else {
                    items.put(it.getType(), it.getAmount());
                }

                it.setAmount(0);
            }

            if (newSoldAmount <= 0 || newSoldPrice <= 0) {
                MESSAGEUTILS.sendLang(player, "nothing-sold");
                return;
            }

            AxSellwandsSellEvent apiEvent = new AxSellwandsSellEvent(player, newSoldPrice, newSoldAmount);
            Bukkit.getPluginManager().callEvent(apiEvent);
            if (apiEvent.isCancelled()) return;
            newSoldPrice = apiEvent.getMoneyMade();

            if (isUnit(block)){
                SimpleEntry<StorageUnit, Location> data = getUnitBySign(block);
                if (data == null) return;
                emptyUnit(data.getValue().getBlock());
            } else if (isBarrel(block)){
                emptyBarrel(block);
                getBarrel(block).updateHologram(block, null, "&cEmpty");
            }

            StringBuilder str = new StringBuilder("[");
            boolean first = true;
            for (Map.Entry<Material, Integer> e : items.entrySet()) {
                if (!first) str.append(", ");
                first = false;
                str.append(e.getValue()).append("x ").append(e.getKey().name());
            }
            str.append("]");
            HistoryUtils.writeToHistory(String.format("%s sold %dx items %s and earned %s (multiplier: %s, uses: %d)", player.getName(), newSoldAmount, str, newSoldPrice, multiplier, uses - 1));

            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("%amount%", "" + newSoldAmount);
            replacements.put("%price%", NumberUtils.formatNumber(newSoldPrice));

            CurrencyIntegration currencyIntegration = CurrencyIntegration.one();
            if (currencyIntegration == null) {
                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF0000[AxSellwands] Failed to sell items, no economy plugin selected in the hooks.yml!"));
                return;
            }
            currencyIntegration.giveBalance(player.getUniqueId(), newSoldPrice);

            if (CONFIG.getBoolean("hologram.enabled", true)) {
                HologramUtils.spawnHologram(player, block.getLocation().add(0.5, 0.5, 0.5), replacements);
            }

            MESSAGEUTILS.sendLang(player, "sell.chat", replacements);

            if (!LANG.getString("sell.actionbar", "").isBlank()) {
                ActionBar.create(StringUtils.format(LANG.getString("sell.actionbar"), replacements)).send(player);
            }

            if (LANG.getSection("sell.title") != null && !LANG.getString("sell.title.title").isBlank()) {
                Title.create(
                        StringUtils.format(LANG.getString("sell.title.title"), replacements),
                        StringUtils.format(LANG.getString("sell.title.subtitle"), replacements), 10, 40, 10
                ).send(player);
            }

            if (!LANG.getString("sounds.sell").isEmpty()) {
                player.playSound(player.getLocation(), Sound.valueOf(LANG.getString("sounds.sell")), 1f, 1f);
            }

            if (!LANG.getString("particles.sell").isEmpty()) {
                player.spawnParticle(Particle.valueOf(LANG.getString("particles.sell")), block.getLocation().add(0.5, 0.5, 0.5), 30, 0.5, 0.5, 0.5);
            }

            if (uses != -1) {
                uses--;

                if (uses < CONFIG.getInt("minimum-durability", 1)) {
                    event.getItem().setAmount(0);
                    return;
                }
            }

            replacements.clear();
            replacements.put("%multiplier%", "" + multiplier);
            replacements.put("%uses%", "" + (uses == -1 ? LANG.getString("unlimited", "∞") : uses));
            replacements.put("%max-uses%", "" + (maxUses == -1 ? LANG.getString("unlimited", "∞") : maxUses));
            replacements.put("%sold-amount%", "" + (soldAmount + newSoldAmount));
            replacements.put("%sold-price%", NumberUtils.formatNumber(soldPrice + newSoldPrice));

            Sellwand wand = Sellwands.getSellwands().get(type);
            ItemBuilder builder = ItemBuilder.create(wand.getItemSection(), replacements);

            event.getItem().setItemMeta(builder.get().getItemMeta());

            wrapper = new NBTWrapper(event.getItem());
            wrapper.set("axsellwands-uuid", uuid);
            wrapper.set("axsellwands-uses", uses);
            wrapper.set("axsellwands-lastused", System.currentTimeMillis());
            wrapper.set("axsellwands-sold-amount", soldAmount + newSoldAmount);
            wrapper.set("axsellwands-sold-price", soldPrice + newSoldPrice);
            wrapper.set("axsellwands-type", type);
            wrapper.set("axsellwands-multiplier", multiplier);
            wrapper.set("axsellwands-max-uses", maxUses);
            wrapper.build();

            if (block.getState() instanceof Container container) container.update();
        } else {
            for (ItemStack it : contents) {
                if (it == null) continue;
                Double price = shopIntegration.getSellPrice(player.getUniqueId(), it);
                if (price == null || price <= 0) continue;
                price *= multiplier;

                newSoldPrice += price;
                newSoldAmount += it.getAmount();
            }

            if (newSoldAmount <= 0 || newSoldPrice <= 0) {
                MESSAGEUTILS.sendLang(player, "nothing-sold");
                return;
            }

            HashMap<String, String> replacements = new HashMap<>();
            replacements.put("%amount%", "" + newSoldAmount);
            replacements.put("%price%", NumberUtils.formatNumber(newSoldPrice));

            MESSAGEUTILS.sendLang(player, "inspect.chat", replacements);

            if (!LANG.getString("inspect.actionbar", "").isBlank()) {
                ActionBar.create(StringUtils.format(LANG.getString("inspect.actionbar"), replacements)).send(player);
            }

            if (LANG.getSection("inspect.title") != null && !LANG.getString("inspect.title.title").isBlank()) {
                Title.create(
                        StringUtils.format(LANG.getString("inspect.title.title"), replacements),
                        StringUtils.format(LANG.getString("inspect.title.subtitle"), replacements),
                        10, 40, 10
                ).send(player);
            }

            if (!LANG.getString("sounds.inspect").isEmpty()) {
                player.playSound(player.getLocation(), Sound.valueOf(LANG.getString("sounds.inspect")), 1f, 1f);
            }

            if (!LANG.getString("particles.inspect").isEmpty()) {
                player.spawnParticle(Particle.valueOf(LANG.getString("particles.inspect")), block.getLocation().add(0.5, 0.5, 0.5), 30, 0.5, 0.5, 0.5);
            }
        }
    }

    // Fluffy Machines API

    private static boolean isBarrel(Block b){
        if (FM.getKey()){
            return BlockStorage.check(b) instanceof Barrel;
        } else return false;
    }

    private static Barrel getBarrel(Block b){
        return (Barrel) BlockStorage.check(b);
    }

    private static ItemStack getBarrelContents(Block b){
        Barrel barrel = getBarrel(b);
        int amount = barrel.getStored(b);
        ItemStack stored = barrel.getStoredItem(b);
        if (stored == null) return null;
        BlockMenu menu = BlockStorage.getInventory(b);
        
        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType().isAir() || item.getType() != stored.getType()) continue;
            amount += item.getAmount();
        }

        if (amount > 0){
            return new ItemStack(stored.getType(), amount);
        } else {
            return null;
        }
    }

    private static void emptyBarrel(Block b){
        Barrel barrel = getBarrel(b);
        if (barrel == null) return;
        BlockMenu menu = BlockStorage.getInventory(b);
        barrel.setStored(b, 0);

        for (int slot : OUTPUT_SLOTS){
            if (menu.getItemInSlot(slot) == null || menu.getItemInSlot(slot).getType().isAir()) continue;
            menu.replaceExistingItem(slot, null);
        }
    }

    // InfinityExpansion API

    private static boolean isUnit(Block b){
        if (BIE.getKey()){
            boolean isSign = getUnitBySign(b) != null;
            return StorageUnitAPI.isUnit(b) || isSign;
        } else if (IE.getKey()){
            boolean isSign = getUnitBySign(b) != null;
            return BlockStorage.check(b) instanceof StorageUnit || isSign;
        } else return false;
    }

    private static StorageUnit getUnit(Block b){
        if (BIE.getKey()){
            return StorageUnitAPI.getUnit(b);
        } else if (IE.getKey()){
            return (StorageUnit) BlockStorage.check(b);
        } else return null;
    }

    private static ItemStack getContents(Block b){
        if (BIE.getKey()){
            return StorageUnitAPI.getContents(b);
        } else if (IE.getKey()){
            BlockMenu menu = BlockStorage.getInventory(b);
            int stored = Integer.parseInt(BlockStorage.getLocationInfo(b.getLocation(), "stored")) ;
            ItemStack item = menu.getItemInSlot(13);
            if (item == null || item.getType().isAir()) return null;
            stored += menu.getItemInSlot(IE_OUTPUT_SLOT).getAmount();
            return new ItemStack(item.getType(), stored);
        } else return null;
    }

    private static void emptyUnit(Block b){
        if (BIE.getKey()){
            StorageUnitAPI.emptyUnit(b);
        } else if (IE.getKey()){
            BlockMenu menu = BlockStorage.getInventory(b);
            menu.replaceExistingItem(IE_OUTPUT_SLOT, null);
            BlockStorage.addBlockInfo(b, "stored", "0");
        }
    }

    private static SimpleEntry<StorageUnit, Location> getUnitBySign(Block b){
        if (BIE.getKey()){
            return StorageUnitAPI.getUnitBySign(b);
        } else if (IE.getKey()){
            if (BlockStorage.check(b) instanceof StorageUnit){
                return new SimpleEntry<>(getUnit(b), b.getLocation());
            } else {
                if (b.getBlockData() instanceof WallSign wallSign){
                    Block unitBlock = b.getRelative(wallSign.getFacing().getOppositeFace());
                    return new SimpleEntry<>(getUnit(unitBlock), unitBlock.getLocation());
                } else if (b.getState() instanceof Sign){
                    Block unitBlock = b.getRelative(0, -1, 0);
                    if (BlockStorage.check(unitBlock) instanceof StorageUnit){
                        return new SimpleEntry<>(getUnit(unitBlock), unitBlock.getLocation());
                    }
                }
                return null;
            }
        } else return null;
    }


    private static boolean isSlimefunItem(Block b){
        return BlockStorage.check(b) != null;
    }
}
