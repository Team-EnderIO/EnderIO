package com.enderio.core.client.render;

import com.enderio.core.common.util.vec.ImmutableVector3d;
import com.enderio.core.common.util.vec.ImmutableVector3f;
import com.enderio.core.common.util.vec.Vector2f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public final class RenderBoundingBox extends AABB {

    public static final RenderBoundingBox UNIT_CUBE = new RenderBoundingBox(0, 0, 0, 1, 1, 1);

    public RenderBoundingBox(BlockPos pos1, BlockPos pos2) {
        super(pos1, pos2);
    }

    public RenderBoundingBox(AABB bb) {
        super(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    public RenderBoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public RenderBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public RenderBoundingBox(ImmutableVector3d min, ImmutableVector3d max) {
        super(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public RenderBoundingBox(BlockPos bc) {
        super(bc.getX(), bc.getY(), bc.getZ(), bc.getX() + 1, bc.getY() + 1, bc.getZ() + 1);
    }

    public RenderBoundingBox expandBy(RenderBoundingBox other) {
        return new RenderBoundingBox(Math.min(minX, other.minX), Math.min(minY, other.minY), Math.min(minZ, other.minZ), Math.max(maxX, other.maxX),
            Math.max(maxY, other.maxY), Math.max(maxZ, other.maxZ));
    }

    public boolean contains(RenderBoundingBox other) {
        return minX <= other.minX && minY <= other.minY && minZ <= other.minZ && maxX >= other.maxX && maxY >= other.maxY && maxZ >= other.maxZ;
    }

    public boolean contains(BlockPos pos) {
        return minX <= pos.getX() && minY <= pos.getY() && minZ <= pos.getZ() && maxX >= pos.getX() && maxY >= pos.getY() && maxZ >= pos.getZ();
    }

    /**
     * Returns <code>true</code> if the given entity's location point is within the bounding box.
     */
    public boolean contains(Entity entity) {
        return minX <= entity.getX() && minY <= entity.getY() && minZ <= entity.getZ() && maxX >= entity.getX() && maxY >= entity.getY()
            && maxZ >= entity.getZ();
    }

    /**
     * Returns <code>true</code> if the given entity's bounding box intersects with the bounding box.
     * <p>
     * Note that this checks Entity#getEntityBoundingBox.
     */
    public boolean intersects(Entity entity) {
        return intersects(entity.getBoundingBox());
    }

    public boolean intersects(RenderBoundingBox other) {
        return other.maxX > this.minX && other.minX < this.maxX && (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ
            && other.minZ < this.maxZ);
    }

    public RenderBoundingBox scale(float xyz) {
        return scale((double) xyz, (double) xyz, (double) xyz);
    }

    public RenderBoundingBox scale(double xyz) {
        return scale(xyz, xyz, xyz);
    }

    public RenderBoundingBox scale(float x, float y, float z) {
        return scale((double) x, (double) y, (double) z);
    }

    public RenderBoundingBox scale(double x, double y, double z) {
        double w = ((maxX - minX) * (1 - x)) / 2;
        double h = ((maxY - minY) * (1 - y)) / 2;
        double d = ((maxZ - minZ) * (1 - z)) / 2;
        return new RenderBoundingBox(minX + w, minY + h, minZ + d, maxX - w, maxY - h, maxZ - d);
    }

    public RenderBoundingBox translate(float x, float y, float z) {
        return new RenderBoundingBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
    }

    public RenderBoundingBox translate(ImmutableVector3d translation) {
        return translate((float) translation.x(), (float) translation.y(), (float) translation.z());
    }

    public RenderBoundingBox translate(ImmutableVector3f vec) {
        return translate(vec.x(), vec.y(), vec.z());
    }

    /**
     * Returns the vertices of the corners for the specified face in counter-clockwise order.
     */
    public List<Vertex> getCornersWithUvForFace(Direction dir) {
        return getCornersWithUvForFace(dir, 0, 1, 0, 1);
    }

    /**
     * Returns the vertices of the corners for the specified face in counter-clockwise order.
     */
    public NonNullList<Vertex> getCornersWithUvForFace(Direction dir, float minU, float maxU, float minV, float maxV) {
        NonNullList<Vertex> result = NonNullList.create();
        switch (dir) {
        case NORTH:
            result.add(new Vertex(new ImmutableVector3d(maxX, minY, minZ), new Vector2f(minU, minV), new ImmutableVector3f(0, 0, -1)));
            result.add(new Vertex(new ImmutableVector3d(minX, minY, minZ), new Vector2f(maxU, minV), new ImmutableVector3f(0, 0, -1)));
            result.add(new Vertex(new ImmutableVector3d(minX, maxY, minZ), new Vector2f(maxU, maxV), new ImmutableVector3f(0, 0, -1)));
            result.add(new Vertex(new ImmutableVector3d(maxX, maxY, minZ), new Vector2f(minU, maxV), new ImmutableVector3f(0, 0, -1)));
            break;
        case SOUTH:
            result.add(new Vertex(new ImmutableVector3d(minX, minY, maxZ), new Vector2f(maxU, minV), new ImmutableVector3f(0, 0, 1)));
            result.add(new Vertex(new ImmutableVector3d(maxX, minY, maxZ), new Vector2f(minU, minV), new ImmutableVector3f(0, 0, 1)));
            result.add(new Vertex(new ImmutableVector3d(maxX, maxY, maxZ), new Vector2f(minU, maxV), new ImmutableVector3f(0, 0, 1)));
            result.add(new Vertex(new ImmutableVector3d(minX, maxY, maxZ), new Vector2f(maxU, maxV), new ImmutableVector3f(0, 0, 1)));
            break;
        case EAST:
            result.add(new Vertex(new ImmutableVector3d(maxX, maxY, minZ), new Vector2f(maxU, maxV), new ImmutableVector3f(1, 0, 0)));
            result.add(new Vertex(new ImmutableVector3d(maxX, maxY, maxZ), new Vector2f(minU, maxV), new ImmutableVector3f(1, 0, 0)));
            result.add(new Vertex(new ImmutableVector3d(maxX, minY, maxZ), new Vector2f(minU, minV), new ImmutableVector3f(1, 0, 0)));
            result.add(new Vertex(new ImmutableVector3d(maxX, minY, minZ), new Vector2f(maxU, minV), new ImmutableVector3f(1, 0, 0)));
            break;
        case WEST:
            result.add(new Vertex(new ImmutableVector3d(minX, minY, minZ), new Vector2f(maxU, minV), new ImmutableVector3f(-1, 0, 0)));
            result.add(new Vertex(new ImmutableVector3d(minX, minY, maxZ), new Vector2f(minU, minV), new ImmutableVector3f(-1, 0, 0)));
            result.add(new Vertex(new ImmutableVector3d(minX, maxY, maxZ), new Vector2f(minU, maxV), new ImmutableVector3f(-1, 0, 0)));
            result.add(new Vertex(new ImmutableVector3d(minX, maxY, minZ), new Vector2f(maxU, maxV), new ImmutableVector3f(-1, 0, 0)));
            break;
        case UP:
            result.add(new Vertex(new ImmutableVector3d(maxX, maxY, maxZ), new Vector2f(minU, minV), new ImmutableVector3f(0, 1, 0)));
            result.add(new Vertex(new ImmutableVector3d(maxX, maxY, minZ), new Vector2f(minU, maxV), new ImmutableVector3f(0, 1, 0)));
            result.add(new Vertex(new ImmutableVector3d(minX, maxY, minZ), new Vector2f(maxU, maxV), new ImmutableVector3f(0, 1, 0)));
            result.add(new Vertex(new ImmutableVector3d(minX, maxY, maxZ), new Vector2f(maxU, minV), new ImmutableVector3f(0, 1, 0)));
            break;
        case DOWN:
        default:
            result.add(new Vertex(new ImmutableVector3d(minX, minY, minZ), new Vector2f(maxU, maxV), new ImmutableVector3f(0, -1, 0)));
            result.add(new Vertex(new ImmutableVector3d(maxX, minY, minZ), new Vector2f(minU, maxV), new ImmutableVector3f(0, -1, 0)));
            result.add(new Vertex(new ImmutableVector3d(maxX, minY, maxZ), new Vector2f(minU, minV), new ImmutableVector3f(0, -1, 0)));
            result.add(new Vertex(new ImmutableVector3d(minX, minY, maxZ), new Vector2f(maxU, minV), new ImmutableVector3f(0, -1, 0)));
            break;
        }
        return result;
    }

    /**
     * Returns the vertices of the corners for the specified face in counter clockwise order, starting with the top left.
     */
    public List<ImmutableVector3d> getCornersForDir(Direction dir) {
        List<ImmutableVector3d> result = new ArrayList<ImmutableVector3d>(4);
        switch (dir) {
        case NORTH:
            result.add(new ImmutableVector3d(minX, maxY, minZ));
            result.add(new ImmutableVector3d(maxX, maxY, minZ));
            result.add(new ImmutableVector3d(maxX, minY, minZ));
            result.add(new ImmutableVector3d(minX, minY, minZ));
            break;
        case SOUTH:
            result.add(new ImmutableVector3d(minX, maxY, maxZ));
            result.add(new ImmutableVector3d(minX, minY, maxZ));
            result.add(new ImmutableVector3d(maxX, minY, maxZ));
            result.add(new ImmutableVector3d(maxX, maxY, maxZ));
            break;
        case EAST:
            result.add(new ImmutableVector3d(maxX, minY, maxZ));
            result.add(new ImmutableVector3d(maxX, minY, minZ));
            result.add(new ImmutableVector3d(maxX, maxY, minZ));
            result.add(new ImmutableVector3d(maxX, maxY, maxZ));
            break;
        case WEST:
            result.add(new ImmutableVector3d(minX, maxY, maxZ));
            result.add(new ImmutableVector3d(minX, maxY, minZ));
            result.add(new ImmutableVector3d(minX, minY, minZ));
            result.add(new ImmutableVector3d(minX, minY, maxZ));
            break;
        case UP:
            result.add(new ImmutableVector3d(maxX, maxY, maxZ));
            result.add(new ImmutableVector3d(maxX, maxY, minZ));
            result.add(new ImmutableVector3d(minX, maxY, minZ));
            result.add(new ImmutableVector3d(minX, maxY, maxZ));
            break;
        case DOWN:
        default:
            result.add(new ImmutableVector3d(minX, minY, maxZ));
            result.add(new ImmutableVector3d(minX, minY, minZ));
            result.add(new ImmutableVector3d(maxX, minY, minZ));
            result.add(new ImmutableVector3d(maxX, minY, maxZ));
            break;
        }
        return result;
    }

    public ImmutableVector3d getBBCenter() {
        return new ImmutableVector3d(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, minZ + (maxZ - minZ) / 2);
    }

    public double sizeX() {
        return Math.abs(maxX - minX);
    }

    public double sizeY() {
        return Math.abs(maxY - minY);
    }

    public double sizeZ() {
        return Math.abs(maxZ - minZ);
    }

    public ImmutableVector3d getMin() {
        return new ImmutableVector3d(minX, minY, minZ);
    }

    public ImmutableVector3d getMax() {
        return new ImmutableVector3d(maxX, maxY, maxZ);
    }

    public double getArea() {
        return sizeX() * sizeY() * sizeZ();
    }

    public RenderBoundingBox fixMinMax() {
        double mnX = minX;
        double mnY = minY;
        double mnZ = minZ;
        double mxX = maxX;
        double mxY = maxY;
        double mxZ = maxZ;
        boolean mod = false;
        if (minX > maxX) {
            mnX = maxX;
            mxX = minX;
            mod = true;
        }
        if (minY > maxY) {
            mnY = maxY;
            mxY = minY;
            mod = true;
        }
        if (minZ > maxZ) {
            mnZ = maxZ;
            mxZ = minZ;
            mod = true;
        }
        if (!mod) {
            return this;
        }
        return new RenderBoundingBox(mnX, mnY, mnZ, mxX, mxY, mxZ);
    }

    public RenderBoundingBox transform(VertexTransform vertexTransform) {
        ImmutableVector3d min = vertexTransform.apply(new ImmutableVector3d(minX, minY, minZ));
        ImmutableVector3d max = vertexTransform.apply(new ImmutableVector3d(maxX, maxY, maxZ));

        return new RenderBoundingBox(Math.min(min.x(), max.x()), Math.min(min.y(), max.y()), Math.min(min.z(), max.z()), Math.max(min.x(), max.x()),
            Math.max(min.y(), max.y()), Math.max(min.z(), max.z()));
    }

    @Override
    public RenderBoundingBox expandTowards(double x, double y, double z) {
        return new RenderBoundingBox(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z);
    }

    @Override
    public RenderBoundingBox setMaxY(double y2) {
        return new RenderBoundingBox(this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
    }

    public RenderBoundingBox expand(double xyz) {
        return new RenderBoundingBox(minX - xyz, minY - xyz, minZ - xyz, maxX + xyz, maxY + xyz, maxZ + xyz);
    }

    @Override
    public Vec3 getCenter() {
        return new Vec3(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
    }

}
