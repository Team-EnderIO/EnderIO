package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    @Override
    protected Integer fromNBT(CompoundTag nbt) {
        return nbt.getInt("value");
    }
}
