package com.artillexstudios.axapi.placeholders;

import java.util.function.Function;

public class PlaceholderTransformer<T, Z> {
    private final Class<T> from;
    private final Class<Z> to;
    private final Function<T, Z> function;

    public PlaceholderTransformer(Class<T> from, Class<Z> to, Function<T, Z> function) {
        this.from = from;
        this.to = to;
        this.function = function;
    }

    public Class<T> from() {
        return this.from;
    }

    public Class<Z> to() {
        return this.to;
    }

    public Function<T, Z> function() {
        return this.function;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlaceholderTransformer)) {
            return false;
        }
        PlaceholderTransformer that = (PlaceholderTransformer) object;
        return java.util.Objects.equals(this.from, that.from) && java.util.Objects.equals(this.to, that.to) && java.util.Objects.equals(this.function, that.function);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(from, to, function);
    }

    @Override
    public String toString() {
        return "PlaceholderTransformer[from=" + this.from + ", to=" + this.to + ", function=" + this.function + "]";
    }

}
