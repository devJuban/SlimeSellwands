package com.artillexstudios.axapi.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Cooldown<T> {
    private final Map<T, Long> expires = new ConcurrentHashMap<>();
    private Cooldown() { }
    public static <T> Cooldown<T> create() { return new Cooldown<>(); }
    public static <T> Cooldown<T> createSynchronized() { return new Cooldown<>(); }
    public boolean hasCooldown(T key) {
        Long until = expires.get(key);
        if (until == null) return false;
        if (System.currentTimeMillis() >= until) { expires.remove(key); return false; }
        return true;
    }
    public void addCooldown(T key, long millis) { expires.put(key, System.currentTimeMillis() + millis); }
}
