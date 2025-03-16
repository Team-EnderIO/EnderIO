package com.enderio.base.common.block;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Blocks hosting a BE using this base class must derive from {@link EIOEntityBlock} or call the same hooks.
 * Provides baseline functionalities to Ender IO Block Entities:
 * - Redstone Power Detection
 * - Forwarding in block events {@link EIOEntityBlock}.
 */
public class EIOBlockEntity extends EnderBlockEntity {

    private boolean isRedstonePowered;
    private boolean isRedstoneDirty;

    public EIOBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (level != null && !level.isClientSide()) {
            if (isRedstoneDirty) {
                updateRedstonePower();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (supportsRedstonePower()) {
            updateRedstonePower();
        }
    }

    public void neighborChanged(Block neighborBlock, BlockPos neighborPos) {
        if (supportsRedstonePower()) {
            isRedstoneDirty = true;
        }
    }

    // region Redstone

    /**
     * Override this method to opt into Redstone power detection.
     */
    protected boolean supportsRedstonePower() {
        return false;
    }

    protected boolean isRedstonePowered() {
        return isRedstonePowered;
    }

    protected void updateRedstonePower() {
        boolean hasPower = level.hasNeighborSignal(worldPosition);
        boolean didChange = isRedstonePowered != hasPower;
        isRedstonePowered = hasPower;
        isRedstoneDirty = false;
    }

    // endregion
}
