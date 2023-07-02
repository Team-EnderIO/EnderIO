package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongDataSlot extends EnderDataSlot<Long> {

    public LongDataSlot(Supplier<Long> getter, Consumer<Long> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("value", getter().get());
        return tag;
    }

    @Override
    protected Long fromNBT(CompoundTag nbt) {
        return nbt.getLong("value");
    }
}
