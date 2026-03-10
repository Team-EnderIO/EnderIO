package com.enderio.core.common.network.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BlockEntityMenuHelper {

    @SafeVarargs
    public static <T extends BlockEntity> T getBlockEntityFrom(FriendlyByteBuf buf, Level level,
            BlockEntityType<? extends T>... types) {
        // noinspection ConstantValue
        if (buf == null) {
            throw new IllegalArgumentException("Null packet buffer when opening menu!");
        }

        BlockPos pos = buf.readBlockPos();

        if (!level.isLoaded(pos)) {
            throw new IllegalStateException(
                    "Unable to open menu for block at " + pos + " as it is not loaded on the client!");
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity == null) {
            throw new IllegalStateException("Unable to find block entity at " + pos + " on the client!");
        }

        for (var type : types) {
            if (blockEntity.getType() == type) {
                // noinspection unchecked
                return (T) blockEntity;
            }
        }

        throw new IllegalStateException("Block entity at " + pos + " is the wrong type on the client!");
    }
}
