package com.enderio.core.common.network.slot;

import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongNetworkDataSlot extends NetworkDataSlot<Long> {
    public LongNetworkDataSlot(Supplier<Long> getter, Consumer<Long> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(Long value) {
        return LongTag.valueOf(value);
    }

    @Override
    protected Long valueFromNBT(Tag nbt) {
        if (nbt instanceof LongTag longTag) {
            return longTag.getAsLong();
        } else {
            throw new IllegalStateException("Invalid long tag was passed over the network.");
        }
    }
}
