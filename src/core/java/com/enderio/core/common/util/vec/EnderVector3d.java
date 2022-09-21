package com.enderio.core.common.util.vec;

import com.mojang.math.Vector3d;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EnderVector3d {

    public static EnderVector3d ZERO = new EnderVector3d(0, 0, 0);
    public static EnderVector3d MAX = new EnderVector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    public static EnderVector3d MIN = new EnderVector3d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

    private double x;
    private double y;
    private double z;

    public EnderVector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EnderVector3d(Vector4d vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    public EnderVector3d(Vector3d vec) {
        this(vec.x, vec.y, vec.z);
    }

    public EnderVector3d copy() {
        return new EnderVector3d(x, y, z);
    }

    public EnderVector3d add(EnderVector3d other) {
        return add(other.x, other.y, other.z);
    }

    public EnderVector3d add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public EnderVector3d sub(EnderVector3d other) {
        return sub(other.x, other.y, other.z);
    }

    public EnderVector3d sub(double x, double y, double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public EnderVector3d negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public EnderVector3d scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        return this;
    }

    public EnderVector3d normalize() {
        return scale(1.0f / Math.sqrt(x * x + y * y + z * z));
    }

    public double dot(EnderVector3d other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(EnderVector3d other) {
        double dx, dy, dz;
        dx = x - other.x;
        dy = y - other.y;
        dz = z - other.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    public double distance(EnderVector3d other) {
        return Math.sqrt(distanceSqr(other));
    }

    public EnderVector3d abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    public EnderVector3d cross(EnderVector3d other) {
        x = this.y * other.z - this.z * other.y;
        y = other.x * this.z - other.z * this.x;
        z = this.x * other.y - this.y * other.x;
        return this;
    }

    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public EnderVector3d withX(double x) {
        return new EnderVector3d(x, y, z);
    }

    public EnderVector3d withY(double y) {
        return new EnderVector3d(x, y, z);
    }

    public EnderVector3d withZ(double z) {
        return new EnderVector3d(x, y, z);
    }

    public EnderVector3d withW(double w) {
        return new EnderVector3d(x, y, z);
    }

    public EnderVector3d set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public EnderVector3d set(EnderVector3d vec) {
        this.x = vec.x();
        this.y = vec.y();
        this.z = vec.z();
        return this;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public Vector3d toVector3d() {
        return new Vector3d(x, y, z);
    }

    @Override
    public String toString() {
        return "EnderVector3d{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof EnderVector3d other) {
            return x == other.x && y == other.y && z == other.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
