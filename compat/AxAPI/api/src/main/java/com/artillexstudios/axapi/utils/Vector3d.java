package com.artillexstudios.axapi.utils;

public class Vector3d {
    private final double x;
    private final double y;
    private final double z;

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Vector3d)) {
            return false;
        }
        Vector3d that = (Vector3d) object;
        return java.util.Objects.equals(this.x, that.x) && java.util.Objects.equals(this.y, that.y) && java.util.Objects.equals(this.z, that.z);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, z);
    }


    public Vector3d() {
        this(0.0D, 0.0D, 0.0D);
    }

    public Vector3d(Vector3f location) {
        this(location.x(), location.y(), location.z());
    }
    @Override
    public String toString() {
        return "Vector3d[x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
    }

}
