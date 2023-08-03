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
     * @param maxTypes for that Direction
     * @return the offset
     * <p>
     *  ###
     *  #1#
     *  ###
     * <p>
     *  #1#
     *  ###
     *  #2#
     * <p>
     *  1##
     *  #2#
     *  ##3
     * <p>
     *  all after 3:
     *  615
     *  294
     *  738
     */

    public static Map<Integer, Vector2i> positions = Util.make(() -> {
        Map<Integer, Vector2i> positions = new HashMap<>();
        positions.put(1, new Vector2i(0, -1));
        positions.put(2, new Vector2i(-1, 0));
        positions.put(3, new Vector2i(0, 1));
        positions.put(4, new Vector2i(1, 0));
        positions.put(5, new Vector2i(1, -1));
        positions.put(6, new Vector2i(-1, -1));
        positions.put(7, new Vector2i(-1, 1));
        positions.put(8, new Vector2i(1, 1));
        positions.put(9, new Vector2i(0, 0));
        return positions;
    });

    public static Vector2i offsetConduit(int typeIndex, int maxTypes) {
        if (typeIndex >= maxTypes) {
            EnderIO.LOGGER.warn(ThrowableUtil.addStackTrace(new IndexOutOfBoundsException("higher index than existing types")));
            return Vector2i.ZERO;
        }
        if (typeIndex < 0) {
            EnderIO.LOGGER.warn(ThrowableUtil.addStackTrace(new IndexOutOfBoundsException("negative index")));
            return Vector2i.ZERO;
        }
        if (maxTypes == 1)
            return Vector2i.ZERO;
        if (maxTypes == 2)
            return typeIndex == 0 ? new Vector2i(0, -1) : new Vector2i(0, 1);
        if (maxTypes == 3) {
            switch (typeIndex) {
                case 0: return new Vector2i(-1, -1);
                case 1: return Vector2i.ZERO;
                case 2: return new Vector2i(1, 1);
            }
        }
        if (maxTypes < 9) {
            Vector2i vector2i = positions.get(typeIndex + 1);
            if (vector2i != null)
                return vector2i;
        }

        EnderIO.LOGGER.warn(ThrowableUtil.addStackTrace(new IndexOutOfBoundsException("fallback was applied")));
        return Vector2i.ZERO;
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
            if (!bundle.getConnection(dir).getConnectedTypes().isEmpty())
                connectedDirs.add(dir);
        }
        if (connectedDirs.isEmpty())
            return Direction.Axis.Z;
        //get Last as MainAxis, because those are the horizontal ones
        return connectedDirs.get(connectedDirs.size()-1).getAxis();
    }
}
