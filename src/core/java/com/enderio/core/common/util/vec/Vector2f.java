package com.enderio.core.common.util.vec;

import net.minecraft.util.Mth;

import java.util.Objects;

public record Vector2f(float x, float y) {

    public static final Vector2f MIN = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
    public static final Vector2f MAX = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector2f IDENTITY = new Vector2f(0f, 0f);

    public Vector2f copy() {
        return new Vector2f(this.x, this.y);
    }

    public Vector2f expand(float amount) {
        return add(amount, amount);
    }

    public Vector2f withX(float x) {
        return new Vector2f(x, this.y);
    }

    public Vector2f withY(float y) {
        return new Vector2f(this.x, y);
    }

    public Vector2f add(Vector2f other) {
        return add(other.x, other.y);
    }

    public Vector2f add(float x, float y) {
        return new Vector2f(this.x + x, this.y + y);
    }

    public Vector2f sub(Vector2f other) {
        return sub(other.x, other.y);
    }

    public Vector2f sub(float x, float y) {
        return new Vector2f(this.x - x, this.y - y);
    }

    public Vector2f negate() {
        return new Vector2f(-this.x, -this.y);
    }

    public Vector2f scale(float scale) {
        return new Vector2f(x * scale, y * scale);
    }

    public Vector2f normalize() {
        return scale(1.0f / Mth.sqrt(x * x + y * y));
    }

    public double lengthSqr() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(Vector2f other) {
        double dx, dy;
        dx = x - other.x;
        dy = y - other.y;
        return (dx * dx + dy * dy);
    }

    public double distance(Vector2f v) {
        return Math.sqrt(distanceSqr(v));
    }

    @Override
    public String toString() {
        return "Vector2f{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Vector2f other) {
            return x == other.x && y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
