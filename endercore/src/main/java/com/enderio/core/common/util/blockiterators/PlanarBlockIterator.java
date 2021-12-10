package com.enderio.core.common.util.blockiterators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nonnull;

public class PlanarBlockIterator extends CubicBlockIterator {
  public enum Orientation {
    EAST_WEST,
    NORTH_SOUTH,
    HORIZONTAL;

    public static Orientation perpendicular(Direction dir) {
      switch (dir) {
      case NORTH:
      case SOUTH:
        return EAST_WEST;
      case EAST:
      case WEST:
        return NORTH_SOUTH;
      case DOWN:
      case UP:
      default:
        return HORIZONTAL;
      }
    }
  }

  private Orientation orientation;

  public PlanarBlockIterator(BlockPos base, Orientation orientation, int radius) {
    super(base, radius);

    this.orientation = orientation;
    switch (orientation) {
    case EAST_WEST:
      curZ = base.getZ();
    case NORTH_SOUTH:
      curX = base.getX();
    case HORIZONTAL:
      curY = base.getY();
    }
  }

  @Override
  public @Nonnull BlockPos next() {
    BlockPos coord = new BlockPos(curX, curY, curZ);
    switch (orientation) {
    case EAST_WEST:
      curY = curY == maxY ? minY : curY + 1;
      curX = curY == minY ? curX + 1 : curX;
    case NORTH_SOUTH:
      curY = curY == maxY ? minY : curY + 1;
      curZ = curY == minY ? curZ + 1 : curZ;
    case HORIZONTAL:
      curX = curX == maxX ? minX : curX + 1;
      curZ = curX == minX ? curZ + 1 : curZ;
    }
    return coord;
  }

  @Override
  public boolean hasNext() {
    return curX <= maxX && curY <= maxY && curZ <= maxZ;
  }
}
