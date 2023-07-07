package com.enderio.core.common.network.slot;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegerNetworkDataSlot extends NetworkDataSlot<Integer> {
    public IntegerNetworkDataSlot(Supplier<Integer> getter, Consumer<Integer> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(Integer value) {
        return IntTag.valueOf(value);
    }

    @Override
    protected Integer valueFromNBT(Tag nbt) {
        if (nbt instanceof IntTag intTag) {
            return intTag.getAsInt();
        } else {
            throw new IllegalStateException("Invalid int tag was passed over the network.");
        }
    }
}
