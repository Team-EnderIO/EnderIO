package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.base.common.util.ThrowableUtil;
import com.enderio.core.common.util.Vector2i;
import net.minecraft.Util;

import java.util.HashMap;
import java.util.Map;

public class OffsetHelper {

    /**
     *
     * Creates an offset based on the Schema
     *
     * @param typeIndex of the type you want to query
     * @param maxTypes for that Direction
     * @return the offset
     *
     *  ###
     *  #1#
     *  ###
     *
     *  #1#
     *  ###
     *  #2#
     *
     *  1##
     *  #2#
     *  ##3
     *
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
}
