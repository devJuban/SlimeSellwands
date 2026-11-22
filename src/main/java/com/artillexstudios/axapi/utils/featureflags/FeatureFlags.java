package com.artillexstudios.axapi.utils.featureflags;

public final class FeatureFlags {
    public static final Flag PACKET_ENTITY_TRACKER_ENABLED = new Flag();
    public static final Flag HOLOGRAM_UPDATE_TICKS = new Flag();
    public static final Flag PACKET_ENTITY_TRACKER_THREADS = new Flag();
    public static final Flag LOGGER_NAME_FORMAT = new Flag();
    private FeatureFlags() { }
    public static final class Flag { public void set(Object ignored) { } }
}
