package com.artillexstudios.axapi.config.adapters.other;

import com.artillexstudios.axapi.config.adapters.TypeAdapter;
import com.artillexstudios.axapi.config.adapters.TypeAdapterHolder;

import java.lang.reflect.Type;
import java.math.BigDecimal;

public final class BigDecimalAdapter implements TypeAdapter<BigDecimal, String> {

    @Override
    public BigDecimal deserialize(TypeAdapterHolder holder, Object input, Type type) {
        if (input instanceof String) { String str = (String) input;
            return new BigDecimal(str);
        } else if (input instanceof Number) { Number number = (Number) input;
            return BigDecimal.valueOf(number.doubleValue());
        }

        throw new IllegalArgumentException(String.format("Can't convert %s into a BigDecimal!", input.getClass()));
    }

    @Override
    public String serialize(TypeAdapterHolder holder, BigDecimal value, Type type) {
        return value.toString();
    }
}
