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

    default boolean canTravelTo() {
        return true;
    }
}
