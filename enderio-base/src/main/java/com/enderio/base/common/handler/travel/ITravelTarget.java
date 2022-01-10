package com.enderio.base.common.handler.travel;

import com.enderio.base.common.util.API;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@API
public interface ITravelTarget {

    ResourceLocation getSerializationName();

    BlockPos getPos();

    CompoundTag save();

    int getItem2BlockRange();
}
