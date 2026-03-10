package com.enderio.core.common.network.menu.payload;

import net.minecraft.network.FriendlyByteBuf;

public record FloatSlotPayload(float value) implements SlotPayload {

    public FloatSlotPayload(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    @Override
    public SlotPayloadType type() {
        return SlotPayloadType.FLOAT;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(value);
    }
}
