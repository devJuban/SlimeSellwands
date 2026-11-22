package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.IntegrationManager;
import com.artillexstudios.axintegrations.types.CustomBlockIntegration;
import com.artillexstudios.axintegrations.utils.IntegrationScheduler;
import io.th0rgal.oraxen.api.OraxenBlocks;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.utils.drops.Drop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
public class OraxenCustomBlock extends CustomBlockIntegration {

    public OraxenCustomBlock() {
        super("Oraxen");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("io.th0rgal.oraxen.api.OraxenBlocks");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isCustomBlock(String itemId) {
        return OraxenBlocks.isOraxenBlock(itemId);
    }

    @Override
    public boolean isCustomBlock(Location location) {
        return OraxenBlocks.isOraxenBlock(location.getBlock());
    }

    @Override
    public boolean place(String itemId, Location location) {
        boolean note = OraxenItems.hasMechanic(itemId, "noteblock");
        boolean string = OraxenItems.hasMechanic(itemId, "stringblock");
        if (!note && !string) return false;

        final Plugin plugin = IntegrationManager.getPlugin();
        IntegrationScheduler.runLater(plugin, () -> {
            if (note) {
                location.getBlock().setType(Material.NOTE_BLOCK);
            } else {
                location.getBlock().setType(Material.TRIPWIRE);
            }
            IntegrationScheduler.runLater(plugin, () -> {
                OraxenBlocks.place(itemId, location);
            }, 1);
        }, 0);
        return true;
    }

    @Override
    public boolean remove(Location location) {
        if (!isCustomBlock(location)) return false;
        OraxenBlocks.remove(location, null, Drop.emptyDrop());
        return true;
    }
}
