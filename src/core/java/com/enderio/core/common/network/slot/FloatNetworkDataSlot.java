package com.enderio.core.common.network.slot;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatNetworkDataSlot extends NetworkDataSlot<Float> {

    public FloatNetworkDataSlot(Supplier<Float> getter, Consumer<Float> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(Float value) {
        return FloatTag.valueOf(value);
    }

    @Override
    protected Float valueFromNBT(Tag nbt) {
        if (nbt instanceof FloatTag floatTag) {
            return floatTag.getAsFloat();
        } else {
            throw new IllegalStateException("Invalid float tag was passed over the network.");
        }
    }
}
