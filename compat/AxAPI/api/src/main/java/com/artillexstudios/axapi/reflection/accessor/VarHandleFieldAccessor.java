package com.artillexstudios.axapi.reflection.accessor;

import com.artillexstudios.axapi.reflection.FieldAccessor;

import java.lang.reflect.Field;

/** Java 8 fallback for the newer VarHandle-backed accessor. */
public class VarHandleFieldAccessor implements FieldAccessor {
    private final Field field;

    public VarHandleFieldAccessor(Field field) throws IllegalAccessException {
        this.field = field;
        field.setAccessible(true);
    }

    @Override
    public void set(Object instance, Object argument) {
        try {
            this.field.set(instance, argument);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void setVolatile(Object instance, Object argument) {
        this.set(instance, argument);
    }

    @Override
    public Object get(Object instance) {
        try {
            return this.field.get(instance);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Object getVolatile(Object instance) {
        return this.get(instance);
    }

    @Override
    public Field getReflectedField() {
        return this.field;
    }
}
