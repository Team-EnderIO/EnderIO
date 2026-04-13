package com.enderio.enderio.api.conduits;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Provides access to neighboring block capabilities.
 */
@ApiStatus.Experimental
public interface ConduitCapabilityAccessor {
    /**
     * Get the desired capability from a neighboring block.
     *
     * @param capability the desired capability.
     * @param neighborSide the side to query for a neighboring capability.
     * @return the capability or null if it is not available.
     */
    @Nullable
    <T, C> T getCapability(BlockCapability<T, C> capability, Direction neighborSide, @Nullable C context);

    @Nullable
    @ApiStatus.NonExtendable
    default <T> T getSidedCapability(BlockCapability<T, Direction> capability, Direction neighborSide) {
        return getCapability(capability, neighborSide, neighborSide.getOpposite());
    }
}
