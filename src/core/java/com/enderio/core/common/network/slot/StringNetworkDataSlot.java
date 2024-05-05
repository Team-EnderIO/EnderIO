package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StringNetworkDataSlot extends NetworkDataSlot<String> {
    public StringNetworkDataSlot(Supplier<String> getter, Consumer<String> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider,String value) {
        return StringTag.valueOf(value);
    }

    @Override
    protected String valueFromNBT(HolderLookup.Provider lookupProvider,Tag nbt) {
        if (nbt instanceof StringTag stringTag) {
            return stringTag.getAsString();
        } else {
            throw new IllegalStateException("Invalid string tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, String value) {
        buf.writeUtf(value);
    }

    @Override
    public String valueFromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            return buf.readUtf();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid string buffer was passed over the network.");
        }
    }
}
