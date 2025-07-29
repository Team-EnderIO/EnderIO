package com.enderio.api.travel;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface ITravelTarget {
    ResourceLocation getSerializationName();

    BlockPos getPos();

    CompoundTag save();

    int getItem2BlockRange();

    int getBlock2BlockRange();

    /**
     * @deprecated No longer used directly, use canTeleportTo and canJumpTo instead. These are more specific to the possible travel types.
     * @return Whether the target can be travelled to.
     */
    @Deprecated(since = "6.2.6-beta")
    default boolean canTravelTo() {
        return true;
    }

    /**
     * @return Whether the target can be teleported to.
     */
    default boolean canTeleportTo() {
        return canTravelTo();
    }

    /**
     * @return Whether the target can be jumped to like an elevator.
     */
    default boolean canJumpTo() {
        return canTravelTo();
    }
}
