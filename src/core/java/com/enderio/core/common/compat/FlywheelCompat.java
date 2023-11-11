package com.enderio.core.common.compat;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class FlywheelCompat {

    @Nullable
    public static BlockEntity getExistingBlockEntity(BlockGetter level, BlockPos pos) {
        if (level instanceof VirtualRenderWorld) {
            return level.getBlockEntity(pos);
        }
        return level.getExistingBlockEntity(pos);
    }
}
