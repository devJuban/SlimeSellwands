package com.artillexstudios.axapi.placeholders;

public class PlaceholderArguments {
    private final PlaceholderArgument<?>[] arguments;

    public PlaceholderArguments(PlaceholderArgument<?>... arguments) {
        this.arguments = arguments;
    }

    public PlaceholderArgument<?>[] arguments() {
        return this.arguments;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PlaceholderArguments)) {
            return false;
        }
        PlaceholderArguments that = (PlaceholderArguments) object;
        return java.util.Objects.equals(this.arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash((Object) arguments);
    }

    @Override
    public String toString() {
        return "PlaceholderArguments[arguments=" + this.arguments + "]";
    }

}
