package com.artillexstudios.axapi.config.adapters.other;

import com.artillexstudios.axapi.config.adapters.TypeAdapter;
import com.artillexstudios.axapi.config.adapters.TypeAdapterHolder;

import java.lang.reflect.Type;
import java.util.UUID;

public final class UUIDAdapter implements TypeAdapter<UUID, String> {

    @Override
    public UUID deserialize(TypeAdapterHolder holder, Object input, Type type) {
        if (input instanceof String) { String str = (String) input;
            return UUID.fromString(str);
        } else if (input instanceof UUID){ UUID uuid = (UUID) input;
            return uuid;
        }

        throw new IllegalArgumentException(String.format("Can't convert %s into a UUID!", input.getClass()));
    }

    @Override
    public String serialize(TypeAdapterHolder holder, UUID value, Type type) {
        return value.toString();
    }
}
