package com.enderio.core.common.util.vec;

import java.util.Objects;

public record Vector4i(int x, int y, int z, int w) {

    public Vector4i copy() {
        return new Vector4i(x, y, z, w);
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
