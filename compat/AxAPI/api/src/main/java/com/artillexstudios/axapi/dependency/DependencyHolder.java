package com.artillexstudios.axapi.dependency;

public class DependencyHolder<T> {
    private final T dependency;
    private final boolean isDefault;

    public DependencyHolder(T dependency, boolean isDefault) {
        this.dependency = dependency;
        this.isDefault = isDefault;
    }

    public T dependency() {
        return this.dependency;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DependencyHolder)) {
            return false;
        }
        DependencyHolder that = (DependencyHolder) object;
        return java.util.Objects.equals(this.dependency, that.dependency) && java.util.Objects.equals(this.isDefault, that.isDefault);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(dependency, isDefault);
    }

    @Override
    public String toString() {
        return "DependencyHolder[dependency=" + this.dependency + ", isDefault=" + this.isDefault + "]";
    }

}
