package com.enderio.core.common.util.vec;

import java.util.Objects;

public record Vector4d(double x, double y, double z, double w) {

    public static Vector4d IDENTITY = new Vector4d(0, 0, 0, 0);

    public Vector4d copy() {
        return new Vector4d(x, y, z, w);
    }

    public Vector4d interpolate(Vector4d destination, double factor) {
        double interpX = (1 - factor) * x + factor * destination.x;
        double interpY = (1 - factor) * y + factor * destination.y;
        double interpZ = (1 - factor) * z + factor * destination.z;
        double interpW = (1 - factor) * w + factor * destination.w;
        return new Vector4d(interpX, interpY, interpZ, interpW);
    }

    public Vector4d add(Vector4d other) {
        return add(other.x, other.y, other.z, other.w);
    }

    public Vector4d add(double x, double y, double z, double w) {
        return new Vector4d(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    public Vector4d sub(Vector4d other) {
        return sub(other.x, other.y, other.z, other.w);
    }

    public Vector4d sub(double x, double y, double z, double w) {
        return new Vector4d(this.x - x, this.y - y, this.z - z, this.w - w);
    }

    public Vector4d negate() {
        return new Vector4d(-x, -y, -z, -w);
    }

    public Vector4d scale(double scale) {
        return new Vector4d(x * scale, y * scale, z * scale, w * scale);
    }

    public Vector4d normalize() {
        return scale(1.0f / Math.sqrt(x * x + y * y + z * z + w * w));
    }

    public double dot(Vector4d other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public double lengthSqr() {
        return x * x + y * y + z * z + w * w;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public ImmutableVector3d toVector3d() {
        return new ImmutableVector3d(x, y, z);
    }

    public Vector4d withX(double x) {
        return new Vector4d(x, y, z, w);
    }

    public Vector4d withY(double y) {
        return new Vector4d(x, y, z, w);
    }

    public Vector4d withZ(double z) {
        return new Vector4d(x, y, z, w);
    }

    public Vector4d withW(double w) {
        return new Vector4d(x, y, z, w);
    }

    @Override
    public String toString() {
        return "Vector4d{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Vector4d other) {
            return x == other.x && y == other.y && z == other.z && w == other.w;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
