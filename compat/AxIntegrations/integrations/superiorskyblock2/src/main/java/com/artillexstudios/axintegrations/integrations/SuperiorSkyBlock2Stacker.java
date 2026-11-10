package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.StackerIntegration;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.handlers.StackedBlocksManager;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
public class SuperiorSkyBlock2Stacker extends StackerIntegration {
    private StackedBlocksManager manager;

    public SuperiorSkyBlock2Stacker() {
        super("SuperiorSkyBlock2");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI");
    }

    @Override
    public boolean setup() {
        manager = SuperiorSkyblockAPI.getStackedBlocks();
        if (manager == null) return false;
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isStacked(Item item) {
        return false;
    }

        @Override
    public Number getStackSize(Item item) {
        return null;
    }

        @Override
    public Number getMaxStackSize(Item item) {
        return null;
    }

    @Override
    public boolean setStackSize(Item item, Number amount) {
        return false;
    }

    @Override
    public boolean isStacked(LivingEntity entity) {
        return false;
    }

        @Override
    public Number getStackSize(LivingEntity entity) {
        return null;
    }

        @Override
    public Number getMaxStackSize(LivingEntity entity) {
        return null;
    }

    @Override
    public boolean setStackSize(LivingEntity entity, Number amount) {
        return false;
    }

    @Override
    public boolean isStacked(CreatureSpawner spawner) {
        return false;
    }

        @Override
    public Number getStackSize(CreatureSpawner spawner) {
        return null;
    }

        @Override
    public Number getMaxStackSize(CreatureSpawner spawner) {
        return null;
    }

    @Override
    public boolean setStackSize(CreatureSpawner spawner, Number amount) {
        return false;
    }

    @Override
    public boolean isStacked(Block block) {
        return manager.getStackedBlockAmount(block) > 0;
    }

        @Override
    public Number getStackSize(Block block) {
        int amount = manager.getStackedBlockAmount(block);
        if (amount <= 0) return null;
        return amount;
    }

        @Override
    public Number getMaxStackSize(Block block) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean setStackSize(Block block, Number amount) {
        if (manager.getStackedBlockAmount(block) <= 0) return false;
        return manager.setStackedBlock(block, amount.intValue());
    }
}
