package com.enderio.core.common.compat;

import dev.engine_room.flywheel.api.visualization.VisualizationLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class FlywheelCompat {

    @Nullable
    public static BlockEntity getExistingBlockEntity(BlockGetter level, BlockPos pos) {
        if (level instanceof VisualizationLevel) {
            return level.getBlockEntity(pos);
        }
        return level.getExistingBlockEntity(pos);
    }
}
