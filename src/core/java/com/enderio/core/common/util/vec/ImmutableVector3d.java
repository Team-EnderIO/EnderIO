package com.enderio.core.common.util.vec;

import net.minecraft.core.BlockPos;

import java.util.Objects;

public record ImmutableVector3d(double x, double y, double z) {

    public static ImmutableVector3d IDENTITY = new ImmutableVector3d(0, 0, 0);
    public static ImmutableVector3d MAX = new ImmutableVector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    public static ImmutableVector3d MIN = new ImmutableVector3d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

    public ImmutableVector3d copy() {
        return new ImmutableVector3d(this.x, this.y, this.z);
    }

    public ImmutableVector3d(BlockPos blockPos) {
        this(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public ImmutableVector3d expand(double amount) {
        return add(amount, amount, amount);
    }

    public ImmutableVector3d withX(double x) {
        return new ImmutableVector3d(x, this.y, this.z);
    }

    public ImmutableVector3d withY(double y) {
        return new ImmutableVector3d(this.x, y, this.z);
    }

    public ImmutableVector3d withZ(double z) {
        return new ImmutableVector3d(this.x, this.y, z);
    }

    public ImmutableVector3d add(ImmutableVector3d other) {
        return add(other.x, other.y, other.z);
    }

    public ImmutableVector3d add(double x, double y, double z) {
        return new ImmutableVector3d(this.x + x, this.y + y, this.z + z);
    }

    public ImmutableVector3d sub(ImmutableVector3d other) {
        return sub(other.x, other.y, other.z);
    }

    public ImmutableVector3d sub(double x, double y, double z) {
        return new ImmutableVector3d(this.x - x, this.y - y, this.z - z);
    }

    public ImmutableVector3d negate() {
        return new ImmutableVector3d(-x, -y, -z);
    }

    public ImmutableVector3d scale(double scale) {
        return new ImmutableVector3d(x * scale, y * scale, z * scale);
    }

    public ImmutableVector3d scale(double sx, double sy, double sz) {
        return new ImmutableVector3d(x * sx, y * sy, z * sz);
    }

    public ImmutableVector3d normalize() {
        return scale(1.0 / Math.sqrt(x * x + y * y + z * z));
    }

    public double dot(ImmutableVector3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public ImmutableVector3d cross(ImmutableVector3d other) {
        double crossX = this.y * other.z - this.z * other.y;
        double crossY = other.x * this.z - other.z * this.x;
        double crossZ = this.x * other.y - this.y * other.x;
        return new ImmutableVector3d(crossX, crossY, crossZ);
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(ImmutableVector3d other) {
        double dx, dy, dz;
        dx = x - other.x;
        dy = y - other.y;
        dz = z - other.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    public double distance(ImmutableVector3d other) {
        return Math.sqrt(distanceSqr(other));
    }

    public ImmutableVector3d abs() {
        return new ImmutableVector3d(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    public com.mojang.math.Vector3d toVec3d() {
        return new com.mojang.math.Vector3d(x, y, z);
    }

    @Override
    public String toString() {
        return "Vector3d{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof ImmutableVector3d other) {
            return x == other.x && y == other.y && z == other.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
