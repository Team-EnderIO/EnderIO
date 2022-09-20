package com.enderio.core.common.util.vec;

import com.mojang.math.Vector3f;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EnderVector3f {

    public static EnderVector3f ZERO = new EnderVector3f(0, 0, 0);

    private float x;
    private float y;
    private float z;

    public EnderVector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EnderVector3f(Vector3f vec) {
        this(vec.x(), vec.y(), vec.z());
    }

    public EnderVector3f copy() {
        return new EnderVector3f(x, y, z);
    }

    public EnderVector3f add(EnderVector3f other) {
        return add(other.x, other.y, other.z);
    }

    public EnderVector3f add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public EnderVector3f sub(EnderVector3f other) {
        return sub(other.x, other.y, other.z);
    }

    public EnderVector3f sub(float x, float y, float z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }

    public EnderVector3f negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    public EnderVector3f scale(double scale) {
        x *= scale;
        y *= scale;
        z *= scale;
        return this;
    }

    public EnderVector3f normalize() {
        return scale(1.0f / Math.sqrt(x * x + y * y + z * z));
    }

    public float dot(EnderVector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public double lengthSqr() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(EnderVector3f other) {
        double dx, dy, dz;
        dx = x - other.x;
        dy = y - other.y;
        dz = z - other.z;
        return (dx * dx + dy * dy + dz * dz);
    }

    public double distance(EnderVector3f other) {
        return Math.sqrt(distanceSqr(other));
    }

    public EnderVector3f abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    public EnderVector3f cross(EnderVector3f other) {
        x = this.y * other.z - this.z * other.y;
        y = other.x * this.z - other.z * this.x;
        z = this.x * other.y - this.y * other.x;
        return this;
    }

    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }

    public EnderVector3f withX(float x) {
        return new EnderVector3f(x, y, z);
    }

    public EnderVector3f withY(float y) {
        return new EnderVector3f(x, y, z);
    }

    public EnderVector3f withZ(float z) {
        return new EnderVector3f(x, y, z);
    }

    public EnderVector3f withW(float w) {
        return new EnderVector3f(x, y, z);
    }

    public EnderVector3f set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }

    @Override
    public String toString() {
        return "EnderVector3f{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof EnderVector3f other) {
            return x == other.x && y == other.y && z == other.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
