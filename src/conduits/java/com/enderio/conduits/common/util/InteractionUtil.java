package com.enderio.conduits.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class InteractionUtil {
    public static Direction fromClickLocation(Vec3 location, Vec3 center) {
        Vec3 offset = location.subtract(center);
        double absX = Math.abs(offset.x);
        double absY = Math.abs(offset.y);
        double absZ = Math.abs(offset.z);
        if (absX >= absY && absX >= absZ) {
            if (offset.x >= 0) {
                return Direction.EAST;
            }
            return Direction.WEST;
        } else if (absY >= absZ) {
            if (offset.y >= 0) {
                return Direction.UP;
            }
            return Direction.DOWN;
        } else {
            if (offset.z >= 0) {
                return Direction.SOUTH;
            }
            return Direction.NORTH;
        }
    }
}
