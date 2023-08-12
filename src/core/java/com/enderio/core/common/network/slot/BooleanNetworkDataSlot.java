package com.enderio.core.common.network.slot;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanNetworkDataSlot extends NetworkDataSlot<Boolean> {

    public BooleanNetworkDataSlot(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(getter, setter);
    }

    @Override
    public Tag serializeValueNBT(Boolean value) {
        return ByteTag.valueOf(value);
    }

    @Override
    protected Boolean valueFromNBT(Tag nbt) {
        if (nbt instanceof ByteTag byteTag) {
            return byteTag.getAsByte() == 1;
        } else {
            throw new IllegalStateException("Invalid boolean tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, Boolean value) {
        buf.writeBoolean(value);
    }

    @Override
    public Boolean valueFromBuffer(FriendlyByteBuf buf) {
        try {
            return buf.readBoolean();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid boolean buffer was passed over the network.");
        }
    }
}
