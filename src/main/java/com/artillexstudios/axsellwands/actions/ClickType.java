package com.artillexstudios.axsellwands.actions;

import org.bukkit.event.block.Action;
import org.jetbrains.annotations.Nullable;

public enum ClickType {
    LEFT,
    RIGHT;

    @Nullable
    public static ClickType from(Action action) {

        if (action == Action.LEFT_CLICK_BLOCK) return LEFT;
        if (action == Action.RIGHT_CLICK_BLOCK) return RIGHT;

        return null;
    }
}