package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.StackerIntegration;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedBlock;
import dev.rosewood.rosestacker.stack.StackedEntity;
import dev.rosewood.rosestacker.stack.StackedItem;
import dev.rosewood.rosestacker.stack.StackedSpawner;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
public class RoseStackerStacker extends StackerIntegration {
    private RoseStackerAPI api;

    public RoseStackerStacker() {
        super("RoseStacker");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("dev.rosewood.rosestacker.api.RoseStackerAPI");
    }

    @Override
    public boolean setup() {
        api = RoseStackerAPI.getInstance();
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isStacked(Item item) {
        return api.isItemStacked(item);
    }

        @Override
    public Number getStackSize(Item item) {
        StackedItem stackedItem = api.getStackedItem(item);
        if (stackedItem == null) return null;
        return stackedItem.getStackSize();
    }

        @Override
    public Number getMaxStackSize(Item item) {
        StackedItem stackedItem = api.getStackedItem(item);
        if (stackedItem == null) return null;
        return getLimit(stackedItem.getStackSettings().getMaxStackSize());
    }

    @Override
    public boolean setStackSize(Item item, Number amount) {
        StackedItem stackedItem = api.getStackedItem(item);
        if (stackedItem == null) return false;
        stackedItem.setStackSize(amount.intValue());
        return true;
    }

    @Override
    public boolean isStacked(LivingEntity entity) {
        return api.isEntityStacked(entity);
    }

        @Override
    public Number getStackSize(LivingEntity entity) {
        StackedEntity stackedEntity = api.getStackedEntity(entity);
        if (stackedEntity == null) return null;
        return stackedEntity.getStackSize();
    }

        @Override
    public Number getMaxStackSize(LivingEntity entity) {
        StackedEntity stackedEntity = api.getStackedEntity(entity);
        if (stackedEntity == null) return null;
        return getLimit(stackedEntity.getStackSettings().getMaxStackSize());
    }

    @Override
    public boolean setStackSize(LivingEntity entity, Number amount) {
        StackedEntity stackedEntity = api.getStackedEntity(entity);
        if (stackedEntity == null) return false;
        int current = stackedEntity.getStackSize();
        int target = amount.intValue();
        if (target > current) {
            stackedEntity.increaseStackSize(target - current, true);
        } else if (target < current) {
            for (int i = 0; i < current - target; i++) {
                stackedEntity.decreaseStackSize();
            }
        }
        return true;
    }

    @Override
    public boolean isStacked(CreatureSpawner spawner) {
        return api.isSpawnerStacked(spawner.getBlock());
    }

        @Override
    public Number getStackSize(CreatureSpawner spawner) {
        StackedSpawner stackedSpawner = api.getStackedSpawner(spawner.getBlock());
        if (stackedSpawner == null) return null;
        return stackedSpawner.getStackSize();
    }

        @Override
    public Number getMaxStackSize(CreatureSpawner spawner) {
        StackedSpawner stackedSpawner = api.getStackedSpawner(spawner.getBlock());
        if (stackedSpawner == null) return null;
        return getLimit(stackedSpawner.getStackSettings().getMaxStackSize());
    }

    @Override
    public boolean setStackSize(CreatureSpawner spawner, Number amount) {
        StackedSpawner stackedSpawner = api.getStackedSpawner(spawner.getBlock());
        if (stackedSpawner == null) return false;
        stackedSpawner.setStackSize(amount.intValue());
        return true;
    }

    @Override
    public boolean isStacked(Block block) {
        return api.isBlockStacked(block);
    }

        @Override
    public Number getStackSize(Block block) {
        StackedBlock stackedBlock = api.getStackedBlock(block);
        if (stackedBlock == null) return null;
        return stackedBlock.getStackSize();
    }

        @Override
    public Number getMaxStackSize(Block block) {
        StackedBlock stackedBlock = api.getStackedBlock(block);
        if (stackedBlock == null) return null;
        return getLimit(stackedBlock.getStackSettings().getMaxStackSize());
    }

    @Override
    public boolean setStackSize(Block block, Number amount) {
        StackedBlock stackedBlock = api.getStackedBlock(block);
        if (stackedBlock == null) return false;
        stackedBlock.setStackSize(amount.intValue());
        return true;
    }

    private Number getLimit(int value) {
        if (value == -1) return Integer.MAX_VALUE;
        return value;
    }
}
