package com.artillexstudios.axapi.config.adapters.primitive;

import com.artillexstudios.axapi.config.adapters.TypeAdapter;
import com.artillexstudios.axapi.config.adapters.TypeAdapterHolder;

import java.lang.reflect.Type;
import java.math.BigInteger;

public final class ShortAdapter implements TypeAdapter<Short, Short> {

    @Override
    public Short deserialize(TypeAdapterHolder registry, Object input, Type type) {
        if (input instanceof Number) { Number num = (Number) input;
            return this.validate(num);
        }

        if (input instanceof String) { String str = (String) input;
            return this.validate(Double.parseDouble(str));
        }

        throw new IllegalArgumentException("Can't make a short from a " + input.getClass());
    }

    private Short validate(Number number) {
        long longValue = number instanceof BigInteger ? ((BigInteger) number).longValueExact() : number.longValue();
        if (longValue > Short.MAX_VALUE || longValue < Short.MIN_VALUE) {
            throw new IllegalArgumentException(String.format("Short value is not actually a short! Should be between %s and %s, but was %s", Short.MIN_VALUE, Short.MAX_VALUE, longValue));
        }

        return (short) longValue;
    }

    @Override
    public Short serialize(TypeAdapterHolder registry, Short value, Type type) {
        return value;
    }
}
