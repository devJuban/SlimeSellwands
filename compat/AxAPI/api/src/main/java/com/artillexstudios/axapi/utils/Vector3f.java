package com.artillexstudios.axapi.utils;

public class Vector3f {
    private final float x;
    private final float y;
    private final float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Vector3f)) {
            return false;
        }
        Vector3f that = (Vector3f) object;
        return java.util.Objects.equals(this.x, that.x) && java.util.Objects.equals(this.y, that.y) && java.util.Objects.equals(this.z, that.z);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, z);
    }


    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(Vector3d location) {
        this((float) location.x(), (float) location.y(), (float) location.z());
    }
    @Override
    public String toString() {
        return "Vector3f[x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
    }

}
