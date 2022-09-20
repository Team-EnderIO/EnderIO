package com.enderio.core.client.render;

import com.enderio.core.common.util.vec.EnderVector3d;
import com.enderio.core.common.util.vec.EnderVector4f;
import com.enderio.core.common.util.vec.Vector2f;

public record Vertex(EnderVector3d xyz, Vector2f uv, ImmutableVector3f normal, EnderVector4f color, int brightness) {

    public Vertex copy() {
        return new Vertex(xyz, uv, normal, color, brightness);
    }

    public Vertex(EnderVector3d xyz, Vector2f uv, ImmutableVector3f normal) {
        this(xyz, uv, normal, null, -1);
    }

    public Vertex(double x, double y, double z, float u, float v, int brightness, float r, float g, float b, float a) {
        this(new EnderVector3d(x, y, z), new Vector2f(u, v), null, new EnderVector4f(r, g, b, a), brightness);
    }

    public Vertex(double x, double y, double z, float u, float v) {
        this(new EnderVector3d(x, y, z), new Vector2f(u, v), null, null, -1);
    }

    public Vertex(double x, double y, double z, float u, float v, float nx, float ny, float nz) {
        this(new EnderVector3d(x, y, z), new Vector2f(u, v), new ImmutableVector3f(nx, ny, nz));
    }

    public void translate(EnderVector3d trans) {
        xyz.add(trans); // TODO: Determine if we want immutable ImmutableVector3d
    }

    public double x() {
        return xyz.x();
    }

    public double y() {
        return xyz.y();
    }

    public double z() {
        return xyz.z();
    }

    public float nx() {
        return normal.x();
    }

    public float ny() {
        return normal.y();
    }

    public float nz() {
        return normal.z();
    }

    public float u() {
        return uv.x();
    }

    public float v() {
        return uv.y();
    }

    public float r() {
        return color.x();
    }

    public float g() {
        return color.y();
    }

    public float b() {
        return color.z();
    }

    public float a() {
        return color.w();
    }

    @Override
    public String toString() {
        return "Vertex{" + "xyz=" + xyz + ", uv=" + uv + ", normal=" + normal + ", color=" + color + ", brightness=" + brightness + '}';
    }
}
