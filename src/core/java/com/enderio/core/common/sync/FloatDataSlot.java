package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatDataSlot extends EnderDataSlot<Float> {

    public FloatDataSlot(Supplier<Float> getter, Consumer<Float> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("value", getter().get());
        return tag;
    }

    @Override
    protected Float fromNBT(CompoundTag nbt) {
        return nbt.getFloat("value");
    }
}
