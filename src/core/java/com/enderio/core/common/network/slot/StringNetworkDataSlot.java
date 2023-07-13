package com.enderio.core.common.network.slot;

import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StringNetworkDataSlot extends NetworkDataSlot<String> {
    public StringNetworkDataSlot(Supplier<String> getter, Consumer<String> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(String value) {
        return StringTag.valueOf(value);
    }

    @Override
    protected String valueFromNBT(Tag nbt) {
        if (nbt instanceof StringTag stringTag) {
            return stringTag.getAsString();
        } else {
            throw new IllegalStateException("Invalid string tag was passed over the network.");
        }
    }
}
