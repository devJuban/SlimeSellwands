package com.artillexstudios.axapi.scheduler;

import com.artillexstudios.axapi.AxPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/** Bukkit scheduler bridge. A region scheduler is used for location-bound delayed work on Folia. */
public final class Scheduler {
    private static final Scheduler INSTANCE = new Scheduler();
    private Scheduler() { }
    public static Scheduler get() { return INSTANCE; }

    public BukkitTask runAsyncTimer(Consumer<BukkitTask> action, long delay, long period) {
        final BukkitTask[] holder = new BukkitTask[1];
        holder[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(AxPlugin.getInstance(), () -> action.accept(holder[0]), delay, period);
        return holder[0];
    }
    public BukkitTask runLaterAsync(Consumer<BukkitTask> action, long delay) {
        final BukkitTask[] holder = new BukkitTask[1];
        holder[0] = Bukkit.getScheduler().runTaskLaterAsynchronously(AxPlugin.getInstance(), () -> action.accept(holder[0]), delay);
        return holder[0];
    }
    public Object runLaterAt(Location location, Consumer<Object> action, long delay) {
        try {
            Object regionScheduler = Bukkit.class.getMethod("getRegionScheduler").invoke(null);
            for (java.lang.reflect.Method method : regionScheduler.getClass().getMethods()) {
                Class<?>[] p = method.getParameterTypes();
                if (!method.getName().equals("runDelayed") || p.length != 4 || !p[0].isInstance(AxPlugin.getInstance()) || p[2] != Location.class) continue;
                Consumer<Object> wrapper = task -> action.accept(task);
                return method.invoke(regionScheduler, AxPlugin.getInstance(), wrapper, location, delay);
            }
        } catch (Throwable ignored) { }
        final BukkitTask[] holder = new BukkitTask[1];
        holder[0] = Bukkit.getScheduler().runTaskLater(AxPlugin.getInstance(), () -> action.accept(holder[0]), delay);
        return holder[0];
    }
}
