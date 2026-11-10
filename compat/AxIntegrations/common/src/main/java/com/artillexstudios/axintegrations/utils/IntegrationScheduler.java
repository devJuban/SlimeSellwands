package com.artillexstudios.axintegrations.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/** Uses Folia's global scheduler when present and falls back to Bukkit on legacy servers. */
public final class IntegrationScheduler {
    private IntegrationScheduler() {
    }

    public static void runLater(Plugin plugin, final Runnable task, long ticks) {
        try {
            Object server = Bukkit.getServer();
            Method schedulerGetter = server.getClass().getMethod("getGlobalRegionScheduler");
            Object scheduler = schedulerGetter.invoke(server);
            for (Method method : schedulerGetter.getReturnType().getMethods()) {
                if (!method.getName().equals("runDelayed") || method.getParameterTypes().length != 3) {
                    continue;
                }
                method.invoke(scheduler, plugin, new Consumer<Object>() {
                    @Override
                    public void accept(Object ignored) {
                        task.run();
                    }
                }, ticks);
                return;
            }
        } catch (ReflectiveOperationException | IllegalArgumentException ignored) {
            // Legacy Bukkit servers use the regular scheduler below.
        }

        Bukkit.getScheduler().runTaskLater(plugin, task, ticks);
    }
}
