package com.enderio.base.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class PosConversionUtil {
    public static ChunkPos blockPosToChunkPos(BlockPos blockPos) {
        return new ChunkPos(
            Math.floorDiv(blockPos.getX(), 16),
            Math.floorDiv(blockPos.getZ(), 16)
        );
    }
}
