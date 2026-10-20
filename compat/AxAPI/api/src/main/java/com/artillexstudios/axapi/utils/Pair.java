package com.artillexstudios.axapi.utils;

public class Pair<T, E> {
    private final T first;
    private final E second;

    public Pair(T first, E second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return this.first;
    }

    public E second() {
        return this.second;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Pair)) {
            return false;
        }
        Pair that = (Pair) object;
        return java.util.Objects.equals(this.first, that.first) && java.util.Objects.equals(this.second, that.second);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(first, second);
    }


    public static <T, E> Pair<T, E> of(T key, E value) {
        return new Pair<>(key, value);
    }

    public static <T, E> Pair<T, E> create(T key, E value) {
        return new Pair<>(key, value);
    }

    public T getKey() {
        return this.first;
    }

    public E getValue() {
        return this.second;
    }
    @Override
    public String toString() {
        return "Pair[first=" + this.first + ", second=" + this.second + "]";
    }

}
