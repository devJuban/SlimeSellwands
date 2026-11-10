package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.BackpackIntegration;
import com.artillexstudios.axshulkers.utils.ShulkerUtils;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class AxShulkersBackpack extends BackpackIntegration {

    public AxShulkersBackpack() {
        super("AxShulkers");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.artillexstudios.axshulkers.utils.ShulkerUtils");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isBackpack(ItemStack item) {
        return ShulkerUtils.getShulkerUUID(item) != null;
    }

        @Override
    public List<ItemStack> getContents(ItemStack item) {
        return java.util.Arrays.asList(ShulkerUtils.getShulkerItems(item));
    }
}
