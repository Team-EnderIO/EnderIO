package com.enderio.core.common.util.blockiterators;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class CubicBlockIterator implements Iterable<BlockPos>, Iterator<BlockPos> {
  protected final int minX, minY, minZ;
  protected final int maxX, maxY, maxZ;
  protected int curX, curY, curZ;

  public CubicBlockIterator(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
    this.minX = curX = minX;
    this.minY = curY = minY;
    this.minZ = curZ = minZ;
    this.maxX = maxX;
    this.maxY = maxY;
    this.maxZ = maxZ;
  }

  public CubicBlockIterator(BlockPos base, int radius) {
    this(base.getX() - radius, base.getY() - radius, base.getZ() - radius, base.getX() + radius, base.getY() + radius, base.getZ() + radius);
  }

  public CubicBlockIterator(BlockPos minPos, BlockPos maxPos) {
    this(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
  }

  public CubicBlockIterator(AABB bb) {
    this((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX - 1, (int) bb.maxY - 1, (int) bb.maxZ - 1);
  }

  @Override
  public @Nonnull BlockPos next() {
    BlockPos ret = new BlockPos(curX, curY, curZ);
    curX = curX == maxX ? minX : curX + 1;
    curY = curX == minX ? curY == maxY ? minY : curY + 1 : curY;
    curZ = curY == minY && curX == minX ? curZ + 1 : curZ;
    return ret;
  }

  @Override
  public boolean hasNext() {
    return curZ <= maxZ;
  }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("You can't remove blocks silly!");
    }

    @Override
    public @Nonnull Iterator<BlockPos> iterator() {
        return this;
    }
}
