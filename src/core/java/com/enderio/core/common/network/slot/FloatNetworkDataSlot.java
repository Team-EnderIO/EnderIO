package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FloatNetworkDataSlot extends NetworkDataSlot<Float> {

    public FloatNetworkDataSlot(Supplier<Float> getter, Consumer<Float> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, Float value) {
        return FloatTag.valueOf(value);
    }

    @Override
    protected Float valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        if (nbt instanceof FloatTag floatTag) {
            return floatTag.getAsFloat();
        } else {
            throw new IllegalStateException("Invalid float tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, Float value) {
        buf.writeFloat(value);
    }

    @Override
    public Float valueFromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            return buf.readFloat();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid float buffer was passed over the network.");
        }
    }
}
