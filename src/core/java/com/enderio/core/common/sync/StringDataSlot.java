package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StringDataSlot extends EnderDataSlot<String> {

    public StringDataSlot(Supplier<String> getter, Consumer<String> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("value", getter().get());
        return tag;
    }

    @Override
    protected String fromNBT(CompoundTag nbt) {
        return nbt.getString("value");
    }
}