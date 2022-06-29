package com.enderio.base.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ResourceLocationDataSlot extends EnderDataSlot<ResourceLocation> {
    public ResourceLocationDataSlot(Supplier<ResourceLocation> getter, Consumer<ResourceLocation> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("rl", getter().get().toString());
        return tag;
    }

    @Override
    protected ResourceLocation fromNBT(CompoundTag nbt) {
        return new ResourceLocation(nbt.getString("rl"));
    }
}
