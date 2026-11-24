package com.artillexstudios.axsellwands.utils.slimefun;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class BlockMenu {

    private final Object menu;

    public BlockMenu(Object menu) {
        this.menu = menu;
    }

    public boolean hasViewer() {
        return Boolean.TRUE.equals(invoke("hasViewer"));
    }

    public ItemStack getItemInSlot(int slot) {
        return (ItemStack) invoke("getItemInSlot", slot);
    }

    public void replaceExistingItem(int slot, ItemStack item) {
        invoke("replaceExistingItem", slot, item);
    }

    private Object invoke(String name, Object... args) {
        try {
            for (Method method : menu.getClass().getMethods()) {
                if (method.getName().equals(name) &&
                        method.getParameterTypes().length == args.length)
                {
                    return method.invoke(menu, args);
                }
            }
        } catch (Throwable ignored) { }
        return null;
    }
}
