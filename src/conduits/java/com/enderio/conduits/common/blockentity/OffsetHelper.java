package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.util.ThrowableUtil;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffsetHelper {

    /**
     *
     * Creates an offset based on the Schema
     *
     * @param typeIndex of the type you want to query
     * @return the offset
     *
     *  1#4
     *  ###
     *  2#3
     */

    public static Vector2i offsetConduit(int typeIndex) {
        if (typeIndex < 0) {
            throw new IndexOutOfBoundsException("negative index");
        }
        return switch (typeIndex) {
            case 0 -> new Vector2i(-1, 1);
            case 1 -> new Vector2i(-1, -1);
            case 2 -> new Vector2i(1, -1);
            case 3 -> new Vector2i(1, 1);
            default -> throw new IllegalStateException("Unexpected value: " + typeIndex);
        };
    }

    public static Vec3i translationFor(Direction.Axis axis, Vector2i offset) {
        return switch (axis) {
            case X -> new Vec3i(0, offset.y(), offset.x());
            case Y -> new Vec3i(offset.x(), 0, offset.y());
            case Z -> new Vec3i(offset.x(), offset.y(), 0);
        };
    }

    public static Direction.Axis findMainAxis(ConduitBundle bundle) {
        List<Direction> connectedDirs = new ArrayList<>();
        for (Direction dir: Direction.values()) {
            if (!bundle.getConnection(dir).getConnectedTypes(bundle).isEmpty())
                connectedDirs.add(dir);
        }
        if (connectedDirs.isEmpty())
            return Direction.Axis.Z;
        //get Last as MainAxis, because those are the horizontal ones
        return connectedDirs.get(connectedDirs.size()-1).getAxis();
    }
}
