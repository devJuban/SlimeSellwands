package com.artillexstudios.axapi.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ItemBuilder {
    private final ItemStack item;
    private ItemBuilder(ItemStack item) { this.item = item; }

    public static ItemBuilder create(Section section, Map<String, String> replacements) {
        String materialName = section.getString("material", "STICK");
        Material material = Material.getMaterial(materialName.toUpperCase(java.util.Locale.ROOT));
        if (material == null) material = Material.STICK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = section.getString("name");
            if (name != null) meta.setDisplayName(StringUtils.format(name, replacements));
            List<String> lore = section.getStringList("lore");
            if (lore != null && !lore.isEmpty()) {
                List<String> formatted = new ArrayList<>();
                for (String line : lore) formatted.add(StringUtils.format(line, replacements));
                meta.setLore(formatted);
            }
            if (section.getBoolean("glow", false)) {
                Enchantment glow = glowEnchantment();
                if (glow != null) meta.addEnchant(glow, 1, true);
                try { meta.addItemFlags(ItemFlag.HIDE_ENCHANTS); } catch (LinkageError ignored) { }
            }
            item.setItemMeta(meta);
        }
        return new ItemBuilder(item);
    }

    private static Enchantment glowEnchantment() {
        try { return (Enchantment) Enchantment.class.getField("DURABILITY").get(null); }
        catch (ReflectiveOperationException ignored) {
            try { return (Enchantment) Enchantment.class.getField("UNBREAKING").get(null); }
            catch (ReflectiveOperationException ignoredAgain) { return null; }
        }
    }

    public ItemStack get() { return item; }
}
