package com.enderio.core.common.util.vec;

import com.mojang.math.Vector4f;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EnderVector4f {

    public static EnderVector4f ZERO = new EnderVector4f(0, 0, 0, 0);

    private float x;
    private float y;
    private float z;
    private float w;

    public EnderVector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public EnderVector4f(Vector4f vec) {
        this(vec.x(), vec.y(), vec.z(), vec.w());
    }

    public EnderVector4f copy() {
        return new EnderVector4f(x, y, z, w);
    }

    public EnderVector4f interpolate(EnderVector4f destination, float factor) {
        x = (1 - factor) * x + factor * destination.x;
        y = (1 - factor) * y + factor * destination.y;
        z = (1 - factor) * z + factor * destination.z;
        w = (1 - factor) * w + factor * destination.w;
        return this;
    }

    public EnderVector4f add(EnderVector4f other) {
        return add(other.x, other.y, other.z, other.w);
    }

    public EnderVector4f add(float x, float y, float z, float w) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.w += w;
        return this;
    }

    public EnderVector4f sub(EnderVector4f other) {
        return sub(other.x, other.y, other.z, other.w);
    }

    public EnderVector4f sub(float x, float y, float z, float w) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w -= w;
        return this;
    }

    public EnderVector4f negate() {
        x = -x;
        y = -y;
        z = -z;
        w = -w;
        return this;
    }

    public EnderVector4f scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        w *= scale;
        return this;
    }

    public EnderVector4f normalize() {
        return scale(1.0f / Math.sqrt(x * x + y * y + z * z + w * w));
    }

    public double dot(EnderVector4f other) {
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

    public EnderVector4f withX(float x) {
        return new EnderVector4f(x, y, z, w);
    }

    public EnderVector4f withY(float y) {
        return new EnderVector4f(x, y, z, w);
    }

    public EnderVector4f withZ(float z) {
        return new EnderVector4f(x, y, z, w);
    }

    public EnderVector4f withW(float w) {
        return new EnderVector4f(x, y, z, w);
    }

    public EnderVector4f set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public float w() {
        return w;
    }

    public Vector4f toVector4f() {
        return new Vector4f(x, y, z, w);
    }

    @Override
    public String toString() {
        return "EnderVector4f{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof EnderVector4f other) {
            return x == other.x && y == other.y && z == other.z && w == other.w;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
