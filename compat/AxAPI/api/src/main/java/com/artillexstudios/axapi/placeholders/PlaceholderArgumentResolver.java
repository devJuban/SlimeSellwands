package com.artillexstudios.axapi.placeholders;

@FunctionalInterface
public interface PlaceholderArgumentResolver<T> {

        T resolve(String string);
}
