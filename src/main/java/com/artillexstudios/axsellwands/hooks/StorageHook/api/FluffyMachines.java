package com.artillexstudios.axsellwands.hooks.StorageHook.api;

import com.artillexstudios.axsellwands.hooks.StorageHook.StorageHook;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getBlockMenu;
import static com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.getSlimefunItem;

public class FluffyMachines implements StorageHook {

    public final int[] OUTPUT_SLOTS = {24,25};

    @Override
    public boolean isStorage(Block b) {
        return getBarrel(b) != null;
    }

    @Override
    public ItemStack getContents(Block b) {
        Object barrel = getBarrel(b);
        if (barrel == null) return null;
        ItemStack stored = (ItemStack) invoke(barrel, "getStoredItem", b);
        if (stored == null) return null;
        com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.BlockMenuAccess menu = getBlockMenu(b);
        if (menu == null) return null;
        int amount = ((Number) invoke(barrel, "getStored", b)).intValue();

        for (int slot : OUTPUT_SLOTS){
            ItemStack item = menu.getItemInSlot(slot);
            if (item == null || item.getType() == org.bukkit.Material.AIR || item.getType() != stored.getType()) continue;
            amount += item.getAmount();
        }

        return new ItemStack(stored.getType(), amount);
    }

    @Override
    public void emptyStorage(Block b) {
        com.artillexstudios.axsellwands.hooks.StorageHook.StorageIntegrationManager.BlockMenuAccess menu = getBlockMenu(b);
        if (menu == null) return;
        Object barrel = getBarrel(b);
        if (barrel == null) return;
        invoke(barrel, "setStored", b, 0);

        for (int slot : OUTPUT_SLOTS){
            menu.replaceExistingItem(slot, null);
        }

        try {
            invoke(barrel, "updateHologram", b, null, "&cEmpty");
        } catch (IllegalStateException ignored) {
            // The Slimefun 5 barrel API no longer exposes this optional presentation method.
        }
    }

    @Nullable
    private Object getBarrel(Block b){
        Object item = getSlimefunItem(b);
        return item != null && item.getClass().getName().endsWith(".Barrel") ? item : null;
    }

    private Object invoke(Object target, String method, Object... args) {
        try {
            for (java.lang.reflect.Method candidate : target.getClass().getMethods()) {
                if (!candidate.getName().equals(method) || candidate.getParameterTypes().length != args.length) continue;
                Class<?>[] types = candidate.getParameterTypes();
                boolean matches = true;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] == null) {
                        if (types[i].isPrimitive()) matches = false;
                    } else if (!wrap(types[i]).isAssignableFrom(args[i].getClass())) {
                        matches = false;
                    }
                }
                if (matches) return candidate.invoke(target, args);
            }
            throw new NoSuchMethodException(method);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unable to call FluffyMachines barrel method " + method, ex);
        }
    }

    private Class<?> wrap(Class<?> type) {
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == boolean.class) return Boolean.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        return type;
    }
}
