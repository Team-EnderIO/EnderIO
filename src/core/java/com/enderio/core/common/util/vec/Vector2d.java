package com.enderio.core.common.util.vec;

import java.util.Objects;

public record Vector2d(double x, double y) {

    public static final Vector2d MIN = new Vector2d(Double.MIN_VALUE, Double.MIN_VALUE);
    public static final Vector2d MAX = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);
    public static final Vector2d IDENTITY = new Vector2d(0, 0);

    public Vector2d copy() {
        return new Vector2d(this.x, this.y);
    }

    public Vector2d expand(double amount) {
        return add(amount, amount);
    }

    public Vector2d withX(double x) {
        return new Vector2d(x, this.y);
    }

    public Vector2d withY(double y) {
        return new Vector2d(this.x, y);
    }

    public Vector2d add(Vector2d other) {
        return add(other.x, other.y);
    }

    public Vector2d add(double x, double y) {
        return new Vector2d(this.x + x, this.y + y);
    }

    public Vector2d sub(Vector2d other) {
        return sub(other.x, other.y);
    }

    public Vector2d sub(double x, double y) {
        return new Vector2d(this.x - x, this.y - y);
    }

    public Vector2d negate() {
        return new Vector2d(-this.x, -this.y);
    }

    public Vector2d scale(double scale) {
        return new Vector2d(x * scale, y * scale);
    }

    public Vector2d normalize() {
        return scale(1.0d / Math.sqrt(x * x + y * y));
    }

    public double lengthSqr() {
        return x * x + y * y;
    }

    public double length() {
        return Math.sqrt(lengthSqr());
    }

    public double distanceSqr(Vector2d other) {
        double dx, dy;
        dx = x - other.x;
        dy = y - other.y;
        return (dx * dx + dy * dy);
    }

    public double distance(Vector2d v) {
        return Math.sqrt(distanceSqr(v));
    }

    @Override
    public String toString() {
        return "Vector2d{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (o instanceof Vector2d other) {
            return x == other.x && y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
