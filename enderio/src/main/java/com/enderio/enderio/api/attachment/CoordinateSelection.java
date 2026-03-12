package com.enderio.enderio.api.attachment;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * This class is in this package, because it's not only used by the item, but also by machines
 */

public record CoordinateSelection(ResourceKey<Level> level, BlockPos pos) {
    public CoordinateSelection(Level level, BlockPos pos) {
        this(level.dimension(), pos);
    }

    /**
     * Get the name of the given level.
     */
    public static String getLevelName(ResourceLocation level) {
        return level.getNamespace().equals("minecraft") ? level.getPath() : level.toString();
    }

    /**
     * Get the name of the level this points to.
     */
    public String getLevelName() {
        return getLevelName(level.location());
    }
}
