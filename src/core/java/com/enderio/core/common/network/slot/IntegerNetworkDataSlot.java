package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegerNetworkDataSlot extends NetworkDataSlot<Integer> {
    public IntegerNetworkDataSlot(Supplier<Integer> getter, Consumer<Integer> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, Integer value) {
        return IntTag.valueOf(value);
    }

    @Override
    protected Integer valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        if (nbt instanceof IntTag intTag) {
            return intTag.getAsInt();
        } else {
            throw new IllegalStateException("Invalid int tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, Integer value) {
        buf.writeInt(value);
    }

    @Override
    public Integer valueFromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            return buf.readInt();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid int buffer was passed over the network.");
        }
    }
}
