package com.artillexstudios.axintegrations.integrations;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axgens.AxGens;
import com.artillexstudios.axgens.api.AxGensAPI;
import com.artillexstudios.axgens.user.AxGensUser;
import com.artillexstudios.axgens.user.Users;
import com.artillexstudios.axintegrations.types.LevelIntegration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.lang.reflect.Method;

public class AxGensLevel extends LevelIntegration {

    public AxGensLevel() {
        super("AxGens");
    }

    @Override
    public boolean canLoad() {
        return ClassUtils.INSTANCE.classExists("com.artillexstudios.axgens.api.AxGensAPI");
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public long getLevel(UUID playerUUID) {
        return AxGensAPI.getLevel(playerUUID);
    }

    @Override
    public double getXP(UUID playerUUID) {
        return 0;
    }

    @Override
    public double getRequiredXP(UUID playerUUID) {
        return 0;
    }

    @Override
    public double getRemainingXP(UUID playerUUID) {
        return 0;
    }

    @Override
    public CompletableFuture<Boolean> setLevel(UUID playerUUID, long amount) {
        AxGensUser user = Users.getUser(playerUUID);
        if (user == null) CompletableFuture.completedFuture(false);
        user.setLevel(amount);
        AxGens.getThreadedQueue().submit(() -> {
            updateDatabase(playerUUID, amount);
        });
        return CompletableFuture.completedFuture(true);
    }

    private void updateDatabase(UUID playerUUID, long amount) {
        try {
            Object database = AxGens.class.getMethod("getDatabase").invoke(null);
            Method updateUser = database.getClass().getMethod("updateUser", UUID.class, long.class);
            updateUser.invoke(database, playerUUID, amount);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Could not update the AxGens user level", error);
        }
    }

    @Override
    public CompletableFuture<Boolean> setXP(UUID playerUUID, double amount) {
        return CompletableFuture.completedFuture(true);
    }
}
