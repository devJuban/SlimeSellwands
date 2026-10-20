package com.artillexstudios.axapi.context;

public class ContextKey<T> {
    private final String name;
    private final Class<T> type;

    public ContextKey(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return this.name;
    }

    public Class<T> type() {
        return this.type;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ContextKey)) {
            return false;
        }
        ContextKey that = (ContextKey) object;
        return java.util.Objects.equals(this.name, that.name) && java.util.Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, type);
    }


    public static <Z> ContextKey<Z> of(String name, Class<Z> type) {
        return new ContextKey<>(name, type);
    }
    @Override
    public String toString() {
        return "ContextKey[name=" + this.name + ", type=" + this.type + "]";
    }

}
