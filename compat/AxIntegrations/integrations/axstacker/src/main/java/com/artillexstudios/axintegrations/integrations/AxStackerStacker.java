package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.StackerIntegration;
import com.artillexstudios.axintegrations.utils.NumberTool;
import com.artillexstudios.axstacker.api.AxStackerAPI;
import com.artillexstudios.axstacker.config.Config;
import com.artillexstudios.axstacker.stack.entity.StackedEntity;
import com.artillexstudios.axstacker.stack.item.StackedItem;
import com.artillexstudios.axstacker.stack.spawner.StackedSpawner;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import java.math.BigInteger;

public class AxStackerStacker extends StackerIntegration {

    public AxStackerStacker() {
        super("AxStacker");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.artillexstudios.axstacker.api.AxStackerAPI");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isStacked(Item item) {
        return AxStackerAPI.getItemStack(item) != null;
    }

        @Override
    public Number getStackSize(Item item) {
        return AxStackerAPI.getItemStackSize(item);
    }

        @Override
    public Number getMaxStackSize(Item item) {
        return getLimit(Config.ITEM_MAX_STACK_SIZE);
    }

    @Override
    public boolean setStackSize(Item item, Number amount) {
        StackedItem stackedItem = AxStackerAPI.getItemStack(item);
        if (stackedItem == null) return false;
        stackedItem.setStackSize(NumberTool.toBigInteger(amount));
        return true;
    }

    @Override
    public boolean isStacked(LivingEntity entity) {
        return AxStackerAPI.getEntityStack(entity) != null;
    }

        @Override
    public Number getStackSize(LivingEntity entity) {
        return AxStackerAPI.getEntityStackSize(entity);
    }

        @Override
    public Number getMaxStackSize(LivingEntity entity) {
        return getLimit(Config.ENTITY_MAX_STACK_SIZE);
    }

    @Override
    public boolean setStackSize(LivingEntity entity, Number amount) {
        StackedEntity stackedEntity = AxStackerAPI.getEntityStack(entity);
        if (stackedEntity == null) return false;
        stackedEntity.setStackSize(NumberTool.toBigInteger(amount));
        return true;
    }

    @Override
    public boolean isStacked(CreatureSpawner spawner) {
        return AxStackerAPI.getSpawnerStack(spawner.getBlock()) != null;
    }

        @Override
    public Number getStackSize(CreatureSpawner spawner) {
        return AxStackerAPI.getSpawnerStackSize(spawner.getBlock());
    }

        @Override
    public Number getMaxStackSize(CreatureSpawner spawner) {
        return getLimit(Config.SPAWNER_MAX_STACK_SIZE);
    }

    @Override
    public boolean setStackSize(CreatureSpawner spawner, Number amount) {
        StackedSpawner stackedSpawner = AxStackerAPI.getSpawnerStack(spawner.getBlock());
        if (stackedSpawner == null) return false;
        stackedSpawner.setStackSize(NumberTool.toBigInteger(amount));
        return true;
    }

    @Override
    public boolean isStacked(Block block) {
        return false;
    }

        @Override
    public Number getStackSize(Block block) {
        return null;
    }

        @Override
    public Number getMaxStackSize(Block block) {
        return null;
    }

    @Override
    public boolean setStackSize(Block block, Number amount) {
        return false;
    }

    private Number getLimit(BigInteger value) {
        if (value.compareTo(BigInteger.ZERO) <= 0) return null;
        return value;
    }
}
