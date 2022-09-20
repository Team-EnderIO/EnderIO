package com.enderio.core.common.util.vec;

import java.util.Objects;

public record Vector2i(int x, int y) {
    public static final Vector2i MIN = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
    public static final Vector2i MAX = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Vector2i IDENTITY = new Vector2i(0, 0);

    public Vector2i add(Vector2i other) {
        return add(other.x, other.y);
    }

    public Vector2i add(int x, int y) {
        return new Vector2i(this.x + x, this.y + y);
    }

    public Vector2i expand(int amount) {
        return add(amount, amount);
    }

    public Vector2i withX(int x) {
        return new Vector2i(x, this.y);
    }

    public Vector2i withY(int y) {
        return new Vector2i(this.x, y);
    }

    public Vector2i sub(Vector2i other) {
        return sub(other.x, other.y);
    }

    public Vector2i sub(int x, int y) {
        return new Vector2i(this.x - x, this.y - y);
    }

    public Vector2i negate() {
        return new Vector2i(-this.x, -this.y);
    }

    public Vector2i scale(int scale) {
        return new Vector2i(this.x * scale, this.y * scale);
    }

    public double lengthSqr() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSquared(Vector2i other) {
        double dx, dy;
        dx = x - other.x;
        dy = y - other.y;
        return (dx * dx + dy * dy);
    }

    public double distance(Vector2i other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public String toString() {
        return "Vector2i{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Vector2i other) {
            return x == other.x && y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
