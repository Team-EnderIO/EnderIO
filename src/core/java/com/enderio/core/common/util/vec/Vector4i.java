package com.enderio.core.common.util.vec;

import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class Vector4i {

    public static Vector4i IDENTITY = new Vector4i(0, 0, 0, 0);

    private int x;
    private int y;
    private int z;
    private int w;

    public Vector4i(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4i copy() {
        return new Vector4i(x, y, z, w);
    }

    public Vector4i add(Vector4i other) {
        return add(other.x, other.y, other.z, other.w);
    }

    public Vector4i add(int x, int y, int z, int w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public Vector4i sub(Vector4i other) {
        return sub(other.x, other.y, other.z, other.w);
    }

    public Vector4i sub(int x, int y, int z, int w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public Vector4i negate() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }

    public Vector4i scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        w *= scale;
        return this;
    }

    public Vector4i normalize() {
        return scale(1.0f / Math.sqrt(x * x + y * y + z * z + w * w));
    }

    public double dot(Vector4i other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public double lengthSqr() {
        return x * x + y * y + z * z + w * w;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public Vector4i withX(int x) {
        return new Vector4i(x, y, z, w);
    }

    public Vector4i withY(int y) {
        return new Vector4i(x, y, z, w);
    }

    public Vector4i withZ(int z) {
        return new Vector4i(x, y, z, w);
    }

    public Vector4i withW(int w) {
        return new Vector4i(x, y, z, w);
    }

    public Vector4i set(int x, int y, int z, int w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public int w() {
        return w;
    }

    @Override
    public String toString() {
        return "Vector4i{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Vector4i other) {
            return x == other.x && y == other.y && z == other.z && w == other.w;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
