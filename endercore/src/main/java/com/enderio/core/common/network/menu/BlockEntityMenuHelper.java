package com.enderio.core.common.network.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityMenuHelper {

    public static <T extends BlockEntity> T getBlockEntityFrom(RegistryFriendlyByteBuf buf, Level level,
            BlockEntityType<? extends T> type) {
        // noinspection ConstantValue
        if (buf == null) {
            throw new IllegalArgumentException("Null packet buffer when opening menu!");
        }

        BlockPos pos = buf.readBlockPos();

        if (!level.isLoaded(pos)) {
            throw new IllegalStateException(
                    "Unable to open menu for block at " + pos + " as it is not loaded on the client!");
        }

        T blockEntity = type.getBlockEntity(level, pos);
        if (blockEntity == null) {
            throw new IllegalStateException("Unable to find block entity at " + pos + " on the client!");
        }

        return blockEntity;
    }
}
