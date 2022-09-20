package com.enderio.core.common.util.vec;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.Objects;

public record ImmutableVector3f(float x, float y, float z) {

    public ImmutableVector3f copy() {
        return new ImmutableVector3f(this.x, this.y, this.z);
    }

    public ImmutableVector3f(BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public ImmutableVector3f expand(float amount) {
        return add(amount, amount, amount);
    }

    public ImmutableVector3f withX(float x) {
        return new ImmutableVector3f(x, this.y, this.z);
    }

    public ImmutableVector3f withY(float y) {
        return new ImmutableVector3f(this.x, y, this.z);
    }

    public ImmutableVector3f withZ(float z) {
        return new ImmutableVector3f(this.x, this.y, z);
    }

    public ImmutableVector3f add(ImmutableVector3f other) {
        return add(other.x, other.y, other.z);
    }

    public ImmutableVector3f add(float x, float y, float z) {
        return new ImmutableVector3f(this.x + x, this.y + y, this.z + z);
    }

    public ImmutableVector3f sub(ImmutableVector3f other) {
        return sub(other.x, other.y, other.z);
    }

    public ImmutableVector3f sub(float x, float y, float z) {
        return new ImmutableVector3f(this.x - x, this.y - y, this.z - z);
    }

    public ImmutableVector3f negate() {
        return new ImmutableVector3f(-x, -y, -z);
    }

    public ImmutableVector3f scale(float scale) {
        return new ImmutableVector3f(x * scale, y * scale, z * scale);
    }

    public ImmutableVector3f scale(float sx, float sy, float sz) {
        return new ImmutableVector3f(x * sx, y * sy, z * sz);
    }

    public ImmutableVector3f normalize() {
        return scale(1.0f / Mth.sqrt(x * x + y * y + z * z));
    }

    public float dot(ImmutableVector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public ImmutableVector3f cross(ImmutableVector3f other) {
        float crossX = this.y * other.z - this.z * other.y;
        float crossY = other.x * this.z - other.z * this.x;
        float crossZ = this.x * other.y - this.y * other.x;
        return new ImmutableVector3f(crossX, crossY, crossZ);
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(ImmutableVector3f other) {
        float dx, dy, dz;
        dx = x - other.x;
        dy = y - other.y;
        dz = z - other.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    public double distance(ImmutableVector3f other) {
        return Math.sqrt(distanceSqr(other));
    }

    public ImmutableVector3f abs() {
        return new ImmutableVector3f(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public com.mojang.math.Vector3f toVec3f() {
        return new com.mojang.math.Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3f{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof ImmutableVector3f other) {
            return x == other.x && y == other.y && z == other.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
