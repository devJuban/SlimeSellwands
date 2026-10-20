package com.artillexstudios.axapi.config.adapters.primitive;

import com.artillexstudios.axapi.config.adapters.TypeAdapter;
import com.artillexstudios.axapi.config.adapters.TypeAdapterHolder;

import java.lang.reflect.Type;
import java.math.BigInteger;

public final class ByteAdapter implements TypeAdapter<Byte, Byte> {

    @Override
    public Byte deserialize(TypeAdapterHolder registry, Object input, Type type) {
        if (input instanceof Number) { Number num = (Number) input;
            return this.validate(num);
        }

        if (input instanceof String) { String str = (String) input;
            return this.validate(Double.parseDouble(str));
        }

        throw new IllegalArgumentException("Can't make a byte from a " + input.getClass());
    }

    private Byte validate(Number number) {
        long longValue = number instanceof BigInteger ? ((BigInteger) number).longValueExact() : number.longValue();
        if (longValue > Byte.MAX_VALUE || longValue < Byte.MIN_VALUE) {
            throw new IllegalArgumentException(String.format("Byte value is not actually a byte! Should be between %s and %s, but was %s", Byte.MIN_VALUE, Byte.MAX_VALUE, longValue));
        }

        return (byte) longValue;
    }

    @Override
    public Byte serialize(TypeAdapterHolder registry, Byte value, Type type) {
        return value;
    }
}
