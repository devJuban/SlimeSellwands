package com.artillexstudios.axapi.config.adapters.primitive;

import com.artillexstudios.axapi.config.adapters.TypeAdapter;
import com.artillexstudios.axapi.config.adapters.TypeAdapterHolder;

import java.lang.reflect.Type;
import java.math.BigInteger;

public final class LongAdapter implements TypeAdapter<Long, Long> {

    @Override
    public Long deserialize(TypeAdapterHolder registry, Object input, Type type) {
        if (input instanceof Number) { Number num = (Number) input;
            return this.getValue(num);
        }

        if (input instanceof String) { String str = (String) input;
            return this.getValue(Double.parseDouble(str));
        }

        throw new IllegalArgumentException("Can't make a long from a " + input.getClass());
    }

    private Long getValue(Number number) {
        return number instanceof BigInteger ? ((BigInteger) number).longValueExact() : number.longValue();
    }

    @Override
    public Long serialize(TypeAdapterHolder registry, Long value, Type type) {
        return value;
    }
}
