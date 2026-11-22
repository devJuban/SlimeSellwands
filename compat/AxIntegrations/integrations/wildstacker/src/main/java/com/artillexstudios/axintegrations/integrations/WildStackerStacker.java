package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axintegrations.types.StackerIntegration;
import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedBarrel;
import com.bgsoftware.wildstacker.api.objects.StackedEntity;
import com.bgsoftware.wildstacker.api.objects.StackedItem;
import com.bgsoftware.wildstacker.api.objects.StackedSpawner;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
public class WildStackerStacker extends StackerIntegration {

    public WildStackerStacker() {
        super("WildStacker");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.bgsoftware.wildstacker.api.WildStackerAPI");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isStacked(Item item) {
        return WildStackerAPI.getStackedItem(item) != null;
    }

        @Override
    public Number getStackSize(Item item) {
        return WildStackerAPI.getItemAmount(item);
    }

        @Override
    public Number getMaxStackSize(Item item) {
        StackedItem stackedItem = WildStackerAPI.getStackedItem(item);
        if (stackedItem == null) return null;
        return stackedItem.getStackLimit();
    }

    @Override
    public boolean setStackSize(Item item, Number amount) {
        StackedItem stackedItem = WildStackerAPI.getStackedItem(item);
        if (stackedItem == null) return false;
        stackedItem.setStackAmount(amount.intValue(), true);
        return true;
    }

    @Override
    public boolean isStacked(LivingEntity entity) {
        return WildStackerAPI.getStackedEntity(entity) != null;
    }

        @Override
    public Number getStackSize(LivingEntity entity) {
        return WildStackerAPI.getEntityAmount(entity);
    }

        @Override
    public Number getMaxStackSize(LivingEntity entity) {
        StackedEntity stackedEntity = WildStackerAPI.getStackedEntity(entity);
        if (stackedEntity == null) return null;
        return stackedEntity.getStackLimit();
    }

    @Override
    public boolean setStackSize(LivingEntity entity, Number amount) {
        StackedEntity stackedEntity = WildStackerAPI.getStackedEntity(entity);
        if (stackedEntity == null) return false;
        stackedEntity.setStackAmount(amount.intValue(), true);
        return true;
    }

    @Override
    public boolean isStacked(CreatureSpawner spawner) {
        return WildStackerAPI.getStackedSpawner(spawner) != null;
    }

        @Override
    public Number getStackSize(CreatureSpawner spawner) {
        return WildStackerAPI.getSpawnersAmount(spawner);
    }

        @Override
    public Number getMaxStackSize(CreatureSpawner spawner) {
        StackedSpawner stackedSpawner = WildStackerAPI.getStackedSpawner(spawner);
        if (stackedSpawner == null) return null;
        return stackedSpawner.getStackLimit();
    }

    @Override
    public boolean setStackSize(CreatureSpawner spawner, Number amount) {
        StackedSpawner stackedSpawner = WildStackerAPI.getStackedSpawner(spawner);
        if (stackedSpawner == null) return false;
        stackedSpawner.setStackAmount(amount.intValue(), true);
        return true;
    }

    @Override
    public boolean isStacked(Block block) {
        return WildStackerAPI.getStackedBarrel(block) != null;
    }

        @Override
    public Number getStackSize(Block block) {
        return WildStackerAPI.getBarrelAmount(block);
    }

        @Override
    public Number getMaxStackSize(Block block) {
        StackedBarrel stackedBarrel = WildStackerAPI.getStackedBarrel(block);
        if (stackedBarrel == null) return null;
        return stackedBarrel.getStackLimit();
    }

    @Override
    public boolean setStackSize(Block block, Number amount) {
        StackedBarrel stackedBarrel = WildStackerAPI.getStackedBarrel(block);
        if (stackedBarrel == null) return false;
        stackedBarrel.setStackAmount(amount.intValue(), true);
        return true;
    }
}
