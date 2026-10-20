package com.artillexstudios.axapi.utils;

public class Quaternion {
    private final float x;
    private final float y;
    private final float z;
    private final float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public float w() {
        return this.w;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Quaternion)) {
            return false;
        }
        Quaternion that = (Quaternion) object;
        return java.util.Objects.equals(this.x, that.x) && java.util.Objects.equals(this.y, that.y) && java.util.Objects.equals(this.z, that.z) && java.util.Objects.equals(this.w, that.w);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Quaternion[x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", w=" + this.w + "]";
    }

}
