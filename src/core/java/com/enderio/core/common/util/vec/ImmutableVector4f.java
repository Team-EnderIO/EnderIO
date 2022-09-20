package com.enderio.core.common.util.vec;

import net.minecraft.util.Mth;

import java.util.Objects;

public record ImmutableVector4f(float x, float y, float z, float w) {

    public static ImmutableVector4f IDENTITY = new ImmutableVector4f(0, 0, 0, 0);

    public ImmutableVector4f copy() {
        return new ImmutableVector4f(x, y, z, w);
    }

    public ImmutableVector4f interpolate(ImmutableVector4f destination, float factor) {
        float interpX = (1 - factor) * x + factor * destination.x;
        float interpY = (1 - factor) * y + factor * destination.y;
        float interpZ = (1 - factor) * z + factor * destination.z;
        float interpW = (1 - factor) * w + factor * destination.w;
        return new ImmutableVector4f(interpX, interpY, interpZ, interpW);
    }

    public ImmutableVector4f add(ImmutableVector4f other) {
        return add(other.x, other.y, other.z, other.w);
    }

    public ImmutableVector4f add(float x, float y, float z, float w) {
        return new ImmutableVector4f(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    public ImmutableVector4f sub(ImmutableVector4f other) {
        return sub(other.x, other.y, other.z, other.w);
    }

    public ImmutableVector4f sub(float x, float y, float z, float w) {
        return new ImmutableVector4f(this.x - x, this.y - y, this.z - z, this.w - w);
    }

    public ImmutableVector4f negate() {
        return new ImmutableVector4f(-x, -y, -z, -w);
    }

    public ImmutableVector4f scale(float scale) {
        return new ImmutableVector4f(x * scale, y * scale, z * scale, w * scale);
    }

    public ImmutableVector4f normalize() {
        return scale(1.0f / Mth.sqrt(x * x + y * y + z * z + w * w));
    }

    public float dot(ImmutableVector4f other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public float lengthSqr() {
        return x * x + y * y + z * z + w * w;
    }

    public float length() {
        return Mth.sqrt(lengthSqr());
    }

    public ImmutableVector3f toVector3f() {
        return new ImmutableVector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector4f{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof ImmutableVector4f other) {
            return x == other.x && y == other.y && z == other.z && w == other.w;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
