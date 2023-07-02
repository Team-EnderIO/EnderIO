package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanDataSlot extends EnderDataSlot<Boolean> {
    public BooleanDataSlot(Supplier<Boolean> getter, Consumer<Boolean> setter, SyncMode mode) {
        super(getter, setter, mode);
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("value", getter().get());
        return tag;
    }

    @Override
    protected Boolean fromNBT(CompoundTag nbt) {
        return nbt.getBoolean("value");
    }
}
