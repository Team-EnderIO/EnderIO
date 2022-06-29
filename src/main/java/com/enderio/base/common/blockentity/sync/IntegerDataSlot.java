package com.enderio.base.common.blockentity.sync;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegerDataSlot extends EnderDataSlot<Integer> {

    public IntegerDataSlot(Supplier<Integer> getter, Consumer<Integer> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("value", getter().get());
        return tag;
    }

    @Override
    protected Integer fromNBT(CompoundTag nbt) {
        return nbt.getInt("value");
    }
}
